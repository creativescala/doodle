package doodle
package effect

import cats.effect.IO
import doodle.algebra.Algebra
import doodle.algebra.Picture
import doodle.core.format.Format
import doodle.core.{Base64 => B64}

/** The Base64 type represent the ability to encode an image as a Base64 String
  * in a given format.
  */
trait Base64[+Alg <: Algebra, Frame, Fmt <: Format] {
  def base64[A](
      description: Frame,
      picture: Picture[Alg, A]
  ): IO[(A, B64[Fmt])]
  def base64[A](picture: Picture[Alg, A]): IO[(A, B64[Fmt])]
}
