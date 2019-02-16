package doodle

import doodle.effect.Renderer

package object svg {
  type Algebra = doodle.svg.algebra.Algebra.type
  type Drawing[A] = doodle.algebra.generic.Finalized[A]
  type Renderable[A] = doodle.algebra.generic.Renderable[A]

  type SvgFrame = doodle.svg.effect.SvgFrame
  type SvgCanvas = doodle.svg.effect.SvgCanvas
  implicit val svgRenderer: Renderer[Algebra, Drawing, SvgFrame, SvgCanvas] =
    doodle.svg.effect.SvgRenderer

  type Image[A] = doodle.algebra.Image[Algebra, Drawing, A]
  object Image {
    def apply(f: Algebra => Drawing[Unit]): Image[Unit] = {
      new Image[Unit] {
        def apply(implicit algebra: Algebra): Drawing[Unit] =
          f(algebra)
      }
    }
  }
}
