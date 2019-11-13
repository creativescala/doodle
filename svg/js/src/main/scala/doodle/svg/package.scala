package doodle

import doodle.effect.Renderer
import doodle.interact.effect.AnimationRenderer

package object svg {
  val js = new doodle.svg.algebra.JsAlgebraModule {}

  // Need to re-export most of the things from JsAlgebraModule because directly
  // extending JsAlgebraModule from the package object leads to a compilation
  // error
  type Algebra[F[_]] = js.Algebra[F]
  val algebraInstance = js.algebraInstance
  type Drawing[A] = js.Drawing[A]
  val Svg = js.Svg
  type Tag = js.Tag
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
