package doodle
package svg
package effect

import cats.effect.IO
import doodle.core.{Color,Point,Transform}
import doodle.algebra.generic.BoundingBox
import monix.reactive.Observable
import monix.reactive.subjects.PublishSubject
import org.scalajs.dom
import scalatags.JsDom

final case class Canvas(target: dom.Node,
                        frame: Frame,
                        background: Option[Color]) {
  import JsDom.all.{Tag => _, _}

  val algebra: Algebra[Drawing] = algebraInstance

  val redraw: Observable[Int] = {
    val subject = PublishSubject[Int]()
    var started = false
    var lastTs = 0.0
    def register(): Unit = {
      val callback: (Double => Unit) = (ts: Double) => {
        if (started) {
          subject.onNext((ts - lastTs).toInt)
        } else {
          subject.onNext(0)
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
    subject
  }

  private val mouseMoveSubject = PublishSubject[Point]
  private def mouseMoveCallback(tx: Transform): dom.MouseEvent => Unit =
    (evt: dom.MouseEvent) => {
      val rect = evt.target.asInstanceOf[dom.Element].getBoundingClientRect()
      val x = evt.clientX - rect.left; //x position within the element.
      val y = evt.clientY - rect.top;
      mouseMoveSubject.onNext(tx(doodle.core.Point(x, y)))
      ()
    }

  val mouseMove: Observable[Point] = mouseMoveSubject

  private var currentBB: BoundingBox = _
  private var svgRoot: dom.Node = _
  /** Get the root <svg> node, creating one if needed. */
  def svgRoot(bb: BoundingBox): dom.Node = {
    def addCallback(tag: Tag, tx: Transform): Tag =
      tag(onmousemove := (mouseMoveCallback(tx)))

    currentBB = bb
    val tx = Svg.inverseClientTransform(currentBB, frame.size)
    if (svgRoot == null) {
      val newRoot = addCallback(Svg.svgTag(bb, frame), tx)
      svgRoot = target.appendChild(newRoot.render)
      svgRoot
    } else {
      frame.size match {
        case Size.FixedSize(_, _) => svgRoot
        case Size.FitToPicture(_) =>
          val newRoot = addCallback(Svg.svgTag(bb, frame), tx).render
          target.replaceChild(newRoot, svgRoot)
          svgRoot = newRoot
          svgRoot
      }
    }
  }

  private var svgChild: dom.Node = _
  def renderChild(svgRoot: dom.Node, nodes: dom.Node): Unit = {
    if (svgChild == null) {
      svgRoot.appendChild(nodes)
      svgChild = nodes
    } else {
      svgRoot.replaceChild(nodes, svgChild)
      svgChild = nodes
    }
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
  def fromFrame(frame: Frame): Canvas = {
    val target = dom.document.getElementById(frame.id)
    if(target == null) {
      throw new java.util.NoSuchElementException(
        s"Doodle SVG Canvas could not be created, as could not find a DOM element with the requested id ${frame.id}")
    } else Canvas(target, frame, frame.background)
  }
}
