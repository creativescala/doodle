package doodle
package svg
package algebra

import cats.{Apply, Semigroup}
import doodle.language.Basic
import doodle.algebra.Layout
import doodle.algebra.generic._
import scalatags.generic.{Bundle, TypedTag}

final case class Algebra[Builder, Output <: FragT, FragT](
    bundle: Bundle[Builder, Output, FragT])
    extends Layout[Finalized[(TypedTag[Builder, Output, FragT], ?), ?]]
    with Shape[Builder, Output, FragT]
    with Path[Builder, Output, FragT]
    with GenericStyle[(TypedTag[Builder, Output, FragT], ?)]
    with GenericTransform[(TypedTag[Builder, Output, FragT], ?)]
    with Basic[Finalized[(TypedTag[Builder, Output, FragT], ?), ?]] {
  type Tag = TypedTag[Builder, Output, FragT]
  type SvgResult[A] = (Tag, A)

  implicit val tagSemigroup = new Semigroup[Tag] {
    def combine(x: Tag, y: Tag): Tag =
      bundle.svgTags.g(x, y)
  }
  // I don't understand why the compiler cannot derive this itself
  implicit val tagApply: Apply[SvgResult] =
    cats.instances.tuple.catsStdFlatMapForTuple2(tagSemigroup)

  val layout = new GenericLayout[SvgResult]()(Apply.apply[SvgResult])

  def on[A](top: Finalized[SvgResult, A], bottom: Finalized[SvgResult, A])(
      implicit s: Semigroup[A]): Finalized[SvgResult, A] =
    layout.on(top, bottom)(s)

  def beside[A](left: Finalized[SvgResult, A], right: Finalized[SvgResult, A])(
      implicit s: Semigroup[A]): Finalized[SvgResult, A] =
    layout.beside(left, right)(s)

  def above[A](top: Finalized[SvgResult, A], bottom: Finalized[SvgResult, A])(
      implicit s: Semigroup[A]): Finalized[SvgResult, A] =
    layout.above(top, bottom)(s)

  def at[A](img: Finalized[SvgResult, A],
            x: Double,
            y: Double): Finalized[SvgResult, A] =
    layout.at(img, x, y)
}
