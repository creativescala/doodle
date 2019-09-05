package doodle
package svg
package algebra

import cats._
import cats.implicits._
import doodle.language.Basic
import doodle.algebra.{Layout,Size}
import doodle.algebra.generic._
import org.scalajs.dom
import scalatags.JsDom

object Algebra
    extends Layout[Drawing]
    with Size[Drawing]
    with MouseOver
    with Shape[dom.Element, dom.Element, dom.Node]
    with Path[dom.Element, dom.Element, dom.Node]
    with GenericStyle[SvgResult]
    with GenericTransform[SvgResult]
    with Basic[Drawing] {
  val bundle = JsDom

  implicit val tagSemigroup = new Semigroup[Tag] {
    def combine(x: Tag, y: Tag): Tag =
      JsDom.svgTags.g(x, y)
  }

  // Layout ----------------------------------------------------------

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

  // Size ------------------------------------------------------------

  val size = new GenericSize[SvgResult]()

  def width[A](image: Finalized[SvgResult, A]): Finalized[SvgResult, Double] =
    size.width(image)

  def height[A](image: Finalized[SvgResult, A]): Finalized[SvgResult, Double] =
    size.height(image)

  def size[A](image: Finalized[SvgResult, A]): Finalized[SvgResult, (Double, Double)] =
    size.size(image)
}
