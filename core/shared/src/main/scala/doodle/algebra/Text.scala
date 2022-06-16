package doodle
package algebra

import doodle.core.font.Font

/** Algebra for creating and styling text.
  */
trait Text[F[_]] extends Algebra[F] {

  /** Specifies the font to use when rendering text
    */
  def font[A](image: F[A], font: Font): F[A]

  /** Render the given String
    */
  def text(text: String): F[Unit]
}

/** Constructors for Text algebra
  */
trait TextConstructor {
  self: BaseConstructor { type Algebra[x[_]] <: Text[x] } =>

  /** Render the given String
    */
  def text(text: String): Picture[Unit] =
    Picture(alg => alg.text(text))
}
