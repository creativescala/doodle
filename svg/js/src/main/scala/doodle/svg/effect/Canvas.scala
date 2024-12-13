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
import cats.effect.Resource
import cats.effect.unsafe.IORuntime
import cats.syntax.all.*
import doodle.core.BoundingBox
import doodle.core.Point
import doodle.core.Transform
import doodle.core.font.Font
import fs2.Stream
import fs2.concurrent.Topic
import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.dom.svg.Rect
import scalatags.JsDom
import scalatags.JsDom.svgAttrs
import scalatags.JsDom.svgTags

final case class Canvas(
    target: dom.Node,
    frame: Frame,
    redrawTopic: Topic[IO, Int],
    mouseClickTopic: Topic[IO, Point],
    mouseMoveTopic: Topic[IO, Point]
) {
  import JsDom.all.{Tag as _, *}

  val nullCallback: Either[Throwable, Unit] => Unit = _ => ()

  val algebra: Algebra =
    new js.JsAlgebra(this, Svg.svgResultApplicative, Svg.svgResultApplicative)

  private val eventListenerOptions = new dom.EventListenerOptions {}
  eventListenerOptions.once = true

  private var redrawStream: Stream[IO, Int] = _
  private var mouseMoveStream: Stream[IO, Point] = _
  private var mouseClickStream: Stream[IO, Point] = _

  {
    var started = false
    var lastTs = 0.0
    redrawStream = Stream.repeatEval(
      IO.async_ { cb =>
        dom.window.requestAnimationFrame(ts =>
          if started then {
            val diff = ts - lastTs
            lastTs = ts
            cb(Right(diff.toInt))
          } else {
            started = true
            cb(Right(0))
          }
        )
        ()
      }
    )
  }

  private def mouseClickCallback(tx: Transform): dom.MouseEvent => Point =
    (evt: dom.MouseEvent) => {
      val rect = evt.target.asInstanceOf[dom.Element].getBoundingClientRect()
      val x = evt.clientX - rect.left; // x position within the element.
      val y = evt.clientY - rect.top;
      tx(doodle.core.Point(x, y))
    }

  private def mouseMoveCallback(tx: Transform): dom.MouseEvent => Point =
    (evt: dom.MouseEvent) => {
      val rect = evt.target.asInstanceOf[dom.Element].getBoundingClientRect()
      val x = evt.clientX - rect.left; // x position within the element.
      val y = evt.clientY - rect.top;
      tx(doodle.core.Point(x, y))
    }

  private def addCallbacks(elt: Element, tx: Transform): Unit = {
    val onMoveCallback = mouseMoveCallback(tx)
    val onMove: Stream[IO, Point] =
      Stream.repeatEval(
        IO.async_(cb =>
          elt.addEventListener(
            "move",
            evt => cb(Right(onMoveCallback(evt.asInstanceOf[dom.MouseEvent]))),
            eventListenerOptions
          )
        )
      )

    val onClickCallback = mouseClickCallback(tx)
    val onClick: Stream[IO, Point] =
      Stream.repeatEval(
        IO.async_(cb =>
          elt.addEventListener(
            "click",
            evt => cb(Right(onClickCallback(evt.asInstanceOf[dom.MouseEvent]))),
            eventListenerOptions
          )
        )
      )

    mouseMoveStream = onMove
    mouseClickStream = onClick
  }

  private var currentBB: BoundingBox = BoundingBox.empty
  private var svgRoot: dom.Node = _
  {
    val tx = Svg.inverseClientTransform(currentBB, frame.size)
    val tag = Svg.svgTag(currentBB, frame).render
    addCallbacks(tag, tx)
    svgRoot = tag
    target.appendChild(svgRoot)
  }

  /** Get the root <svg> node, creating one if needed. Set mouseMoveStream and
    * mouseClickStream as a side-effect.
    */
  def svgRoot(bb: BoundingBox): dom.Node = {
    currentBB = bb
    val tx = Svg.inverseClientTransform(currentBB, frame.size)
    frame.size match {
      case Size.FixedSize(_, _) => svgRoot
      case Size.FitToPicture(_) =>
        val newRoot = Svg.svgTag(bb, frame).render
        // TODO: do the Right Thing when replacing existing callbacks
        addCallbacks(newRoot, tx)
        target.replaceChild(newRoot, svgRoot)
        svgRoot = newRoot
        svgRoot
    }
  }

  /** The stream that runs everything the Canvas' internals need to work. You
    * must make sure this is executed if you create a Canvas by hand.
    */
  val stream: Stream[IO, Nothing] = {
    val redraw = redrawStream.through(redrawTopic.publish).drain
    val click = mouseClickStream.through(mouseClickTopic.publish).drain
    val move = mouseMoveStream.through(mouseMoveTopic.publish).drain

    redraw.merge(click).merge(move)
  }

  val redraw: Stream[IO, Int] = redrawTopic.subscribe(4)
  val mouseClick: Stream[IO, Point] = mouseClickTopic.subscribe(4)
  val mouseMove: Stream[IO, Point] = mouseMoveTopic.subscribe(4)

  private var svgChild: dom.Node = _
  def renderChild(svgRoot: dom.Node, nodes: dom.Node): Unit = {
    if svgChild == null || !svgRoot.contains(svgChild) then {
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
  def fromFrame(
      frame: Frame
  )(implicit runtime: IORuntime): Resource[IO, Canvas] = {
    val target = dom.document.getElementById(frame.id)
    if target == null then {
      throw new java.util.NoSuchElementException(
        s"Doodle SVG Canvas could not be created, as could not find a DOM element with the requested id ${frame.id}"
      )
    } else {
      (Topic[IO, Int], Topic[IO, Point], Topic[IO, Point])
        .mapN { (redrawTopic, mouseClickTopic, mouseMoveTopic) =>
          Canvas(
            target,
            frame,
            redrawTopic,
            mouseClickTopic,
            mouseMoveTopic
          )
        }
        .toResource
        .flatMap(canvas => canvas.stream.compile.drain.background.as(canvas))
    }
  }
}
