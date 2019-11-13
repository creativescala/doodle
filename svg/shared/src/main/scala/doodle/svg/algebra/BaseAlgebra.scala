package doodle
package svg
package algebra

import cats.Semigroup
import doodle.language.Basic
import doodle.algebra.{Layout, Size}
import doodle.algebra.generic._
import doodle.svg.effect.SvgModule

trait AlgebraModule {
  self: Base with ShapeModule with PathModule with SvgModule =>
  trait BaseAlgebra
      extends doodle.algebra.Algebra[Drawing]
      with Layout[Drawing]
      with Size[Drawing]
      with Shape
      with Path
      with GenericStyle[SvgResult]
      with GenericTransform[SvgResult]
      with Basic[Drawing] {

    val layout = new GenericLayout[SvgResult]()(Svg.svgResultApply)

    def on[A](top: Finalized[SvgResult, A], bottom: Finalized[SvgResult, A])(
        implicit s: Semigroup[A]
    ): Finalized[SvgResult, A] =
      layout.on(top, bottom)(s)

    def beside[A](
        left: Finalized[SvgResult, A],
        right: Finalized[SvgResult, A]
    )(implicit s: Semigroup[A]): Finalized[SvgResult, A] =
      layout.beside(left, right)(s)

    def above[A](top: Finalized[SvgResult, A], bottom: Finalized[SvgResult, A])(
        implicit s: Semigroup[A]
    ): Finalized[SvgResult, A] =
      layout.above(top, bottom)(s)

    def at[A](
        img: Finalized[SvgResult, A],
        x: Double,
        y: Double
    ): Finalized[SvgResult, A] =
      layout.at(img, x, y)

    // Size ------------------------------------------------------------

    val size = new GenericSize[SvgResult]()(Svg.svgResultApply)

    def width[A](image: Finalized[SvgResult, A]): Finalized[SvgResult, Double] =
      size.width(image)

    def height[A](
        image: Finalized[SvgResult, A]
    ): Finalized[SvgResult, Double] =
      size.height(image)

    def size[A](
        image: Finalized[SvgResult, A]
    ): Finalized[SvgResult, (Double, Double)] =
      size.size(image)
  }
}
