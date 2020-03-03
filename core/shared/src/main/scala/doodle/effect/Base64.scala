package doodle
package effect

import cats.effect.IO
import doodle.algebra.{Algebra, Picture}
import doodle.core.{Base64 => B64}

/**
 * The Base64 type represent the ability to encode an image as a Base64 String
 * in a given format.
 */
trait Base64[+Alg[x[_]] <: Algebra[x], F[_], Frame, Format] {
  def base64[A](description: Frame, picture: Picture[Alg, F, A]): IO[(A, B64[Format])]
  def base64[A](picture: Picture[Alg, F, A]): IO[(A, B64[Format])]
}
