/*
 * Copyright 2015 Creative Scala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package doodle
package svg
package effect

import cats.effect.IO
import cats.effect.std.Queue
import cats.effect.unsafe.IORuntime
import doodle.core.BoundingBox
import doodle.core.Color
import doodle.core.Point
import doodle.core.Transform
import doodle.core.font.Font
import fs2.Stream
import org.scalajs.dom
import org.scalajs.dom.svg.Rect
import scalatags.JsDom
import scalatags.JsDom.svgAttrs
import scalatags.JsDom.svgTags

final case class Canvas(
    target: dom.Node,
    frame: Frame,
    background: Option[Color],
    redrawQueue: Queue[IO, Int],
    mouseClickQueue: Queue[IO, Point],
    mouseMoveQueue: Queue[IO, Point]
)(implicit runtime: IORuntime) {
  import JsDom.all.{Tag => _, _}

  val nullCallback: Either[Throwable, Unit] => Unit = _ => ()

  val algebra: Algebra =
    new js.JsAlgebra(this, Svg.svgResultApplicative, Svg.svgResultApplicative)

  val redraw: Stream[IO, Int] = Stream.fromQueueUnterminated(redrawQueue)

  {
    var started = false
    var lastTs = 0.0
    def register(): Unit = {
      val callback: (Double => Unit) = (ts: Double) => {
        if (started) {
          redrawQueue.offer((ts - lastTs).toInt).unsafeRunAsync(nullCallback)
        } else {
          redrawQueue.offer(0)
          started = true
        }
        lastTs = ts
        register()
        ()
      }
      val _ = dom.window.requestAnimationFrame(callback)
      ()
    }

    register()
  }

  private def mouseClickCallback(tx: Transform): dom.MouseEvent => Unit =
    (evt: dom.MouseEvent) => {
      val rect = evt.target.asInstanceOf[dom.Element].getBoundingClientRect()
      val x = evt.clientX - rect.left; //x position within the element.
      val y = evt.clientY - rect.top;
      mouseClickQueue
        .offer(tx(doodle.core.Point(x, y)))
        .unsafeRunAsync(nullCallback)
      ()
    }

  val mouseClick: Stream[IO, Point] =
    Stream.fromQueueUnterminated(mouseClickQueue)

  private def mouseMoveCallback(tx: Transform): dom.MouseEvent => Unit =
    (evt: dom.MouseEvent) => {
      val rect = evt.target.asInstanceOf[dom.Element].getBoundingClientRect()
      val x = evt.clientX - rect.left; //x position within the element.
      val y = evt.clientY - rect.top;
      mouseMoveQueue
        .offer(tx(doodle.core.Point(x, y)))
        .unsafeRunAsync(nullCallback)
      ()
    }

  val mouseMove: Stream[IO, Point] =
    Stream.fromQueueUnterminated(mouseMoveQueue)

  private var currentBB: BoundingBox = _
  private var svgRoot: dom.Node = _

  /** Get the root <svg> node, creating one if needed. */
  def svgRoot(bb: BoundingBox): dom.Node = {
    def addCallbacks(tag: Tag, tx: Transform): Tag =
      tag(
        onmousemove := (mouseMoveCallback(tx)),
        onclick := (mouseClickCallback(tx))
      )

    currentBB = bb
    val tx = Svg.inverseClientTransform(currentBB, frame.size)
    if (svgRoot == null) {
      val newRoot = addCallbacks(Svg.svgTag(bb, frame), tx)
      svgRoot = target.appendChild(newRoot.render)
      svgRoot
    } else {
      frame.size match {
        case Size.FixedSize(_, _) => svgRoot
        case Size.FitToPicture(_) =>
          val newRoot = addCallbacks(Svg.svgTag(bb, frame), tx).render
          target.replaceChild(newRoot, svgRoot)
          svgRoot = newRoot
          svgRoot
      }
    }
  }

  private var svgChild: dom.Node = _
  def renderChild(svgRoot: dom.Node, nodes: dom.Node): Unit = {
    if (svgChild == null || !svgRoot.contains(svgChild)) {
      svgRoot.appendChild(nodes)
      svgChild = nodes
    } else {
      svgRoot.replaceChild(nodes, svgChild)
      svgChild = nodes
    }
  }

  def textBoundingBox(text: String, font: Font): (BoundingBox, Rect) = {
    // Create an invisible SVG element to measure the text size and delete it
    // after use
    //
    // Can't use 'display: none' style beause Firefox will raise an exception if
    // we ask for the bounding box of such an element.
    val elt =
      target.appendChild(svgTags.svg(svgAttrs.visibility := "hidden").render)
    val txt = elt.appendChild(Svg.textTag(text, font).render)
    val bb = txt.asInstanceOf[dom.svg.Text].getBBox()
    val boundingBox = BoundingBox.centered(bb.width, bb.height)

    target.removeChild(elt)
    (boundingBox, bb)
  }

  def render[A](picture: Picture[A]): IO[A] = {
    for {
      result <- Svg.renderWithoutRootTag[Algebra, A](algebra, picture)
    } yield {
      val (bb, tags, a) = result
      val root = svgRoot(bb)
      val _ = renderChild(root, tags.render)
      a
    }
  }
}
object Canvas {
  def fromFrame(frame: Frame)(implicit runtime: IORuntime): IO[Canvas] = {
    import cats.implicits._
    def eventQueue[A]: IO[Queue[IO, A]] = Queue.circularBuffer[IO, A](1)

    (eventQueue[Int], eventQueue[Point], eventQueue[Point]).mapN {
      (redrawQueue, mouseClickQueue, mouseMoveQueue) =>
        val target = dom.document.getElementById(frame.id)
        if (target == null) {
          throw new java.util.NoSuchElementException(
            s"Doodle SVG Canvas could not be created, as could not find a DOM element with the requested id ${frame.id}"
          )
        } else
          Canvas(
            target,
            frame,
            frame.background,
            redrawQueue,
            mouseClickQueue,
            mouseMoveQueue
          )
    }
  }
}
