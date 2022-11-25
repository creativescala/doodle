package doodle
package algebra

import doodle.core.font.Font

/** Algebra for creating and styling text.
  */
trait Text extends Algebra {

  /** Specifies the font to use when rendering text
    */
  def font[A](image: Drawing[A], font: Font): Drawing[A]

  /** Render the given String
    */
  def text(text: String): Drawing[Unit]
}

/** Constructors for Text algebra
  */
trait TextConstructor {
  self: BaseConstructor { type Algebra <: Text } =>

  /** Render the given String
    */
  def text(text: String): Picture[Unit] =
    new Picture[Unit] {
      def apply(implicit algebra: Algebra): algebra.Drawing[Unit] =
        algebra.text(text)
    }
}
