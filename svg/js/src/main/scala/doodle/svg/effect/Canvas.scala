package doodle
package svg
package effect

import cats.effect.IO
import doodle.core.Color
import doodle.algebra.generic.BoundingBox
import monix.reactive.Observable
import monix.reactive.subjects.PublishSubject
import org.scalajs.dom
import scalatags.JsDom

final case class Canvas(target: dom.Node,
                        size: Size,
                        background: Option[Color]) {

  val algebra = doodle.svg.algebra.Algebra
  val svg = Svg(JsDom)

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

  var svgRoot: dom.Node = _

  /** Get the root <svg> node, creating one if needed. */
  def svgRoot(bb: BoundingBox): dom.Node =
    if (svgRoot == null) {
      svgRoot = target.appendChild(svg.svgTag(bb, size).render)
      svgRoot
    } else {
      size match {
        case Size.FixedSize(_, _) => svgRoot
        case Size.FitToPicture(_) =>
          val newRoot = svg.svgTag(bb, size).render
          target.replaceChild(newRoot, svgRoot)
          svgRoot = newRoot
          svgRoot
      }
    }

  var svgChild: dom.Node = _
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
      result <- svg.renderWithoutRootTag[Algebra, A](algebra, picture)
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
    Canvas(target, frame.size, frame.background)
  }
}
