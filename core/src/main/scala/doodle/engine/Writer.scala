package doodle
package engine

import cats.effect.IO
import doodle.algebra.Image
import java.io.File

/** The `Writer` typeclass represents write an image to a file in a given format. */
trait Writer[+Algebra,F[_],Format]{
  def write[A,Alg >: Algebra](file: File, description: Frame, image: Image[Alg,F,A]): IO[A]
}
object Writer {
  /* Standard format type for PDF writer */
  sealed trait Pdf
  /* Standard format type for GIF writer */
  sealed trait Gif
  /* Standard format type for PNG writer */
  sealed trait Png
  /* Standard format type for SVG writer */
  sealed trait Svg
  /* Standard format type for JPEG writer */
  sealed trait Jpg
}
