package doodle

import doodle.effect.Renderer
import doodle.interact.algebra.MouseOver
import doodle.interact.effect.AnimationRenderer
import doodle.language.Basic
// import monix.reactive.Observer
import org.scalajs.dom

package object svg {
  type Algebra[F[_]] = doodle.algebra.Algebra[F] with Basic[F] with MouseOver[F]
  type Tag = scalatags.generic.TypedTag[dom.Element, dom.Element, dom.Node]
  type SvgResult[A] = (Tag, A)
  type Drawing[A] = doodle.algebra.generic.Finalized[SvgResult, A]

  type Frame = doodle.svg.effect.Frame
  type Canvas = doodle.svg.effect.Canvas
  implicit val svgRenderer: Renderer[Algebra, Drawing, Frame, Canvas] =
    doodle.svg.effect.SvgRenderer
  implicit val svgAnimationRenderer: AnimationRenderer[Canvas] =
    doodle.svg.effect.SvgAnimationRenderer
  implicit val svgCanvas: doodle.svg.algebra.CanvasAlgebra =
    doodle.svg.algebra.CanvasAlgebra

  implicit val svgScheduler = monix.execution.Scheduler.global

  val Frame = doodle.svg.effect.Frame

  type Picture[A] = doodle.algebra.Picture[Algebra, Drawing, A]
  object Picture {
    def apply(f: Algebra[Drawing] => Drawing[Unit]): Picture[Unit] = {
      new Picture[Unit] {
        def apply(implicit algebra: Algebra[Drawing]): Drawing[Unit] =
          f(algebra)
      }
    }
  }
}
