package doodle

import doodle.effect.Renderer
import doodle.algebra.generic.reified.Reification
import doodle.language.Basic

package object svg {
  type Algebra[F[_]] = doodle.algebra.Algebra[F] with Basic[F]
  type Drawing[A] = doodle.algebra.generic.Finalized[Reification,A]
  type Renderable[A] = doodle.algebra.generic.Renderable[Reification,A]

  type Frame = doodle.svg.effect.Frame
  type Canvas = doodle.svg.effect.Canvas
  implicit val svgRenderer: Renderer[Algebra, Drawing, Frame, Canvas] =
    doodle.svg.effect.SvgRenderer

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
