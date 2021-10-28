package doodle
package java2d

import cats.effect.unsafe.implicits.global
import doodle.algebra.ToPicture
import doodle.core.{Base64 => B64}
import doodle.effect.Writer._
import doodle.effect._
import doodle.syntax._
import minitest._

object ToPictureSpec extends SimpleTestSuite {
  def base64Distance[A](b1: B64[A], b2: B64[A]): Double = {
    import java.util.{Base64 => JBase64}
    val d1 = JBase64.getDecoder().decode(b1.value)
    val d2 = JBase64.getDecoder().decode(b2.value)

    d1.zip(d2).foldLeft(0.0) { (accum, elts) =>
      val (byte1, byte2) = elts
      accum + Math.abs(byte1 - byte2)
    }
  }

  val image = square[Algebra, Drawing](40.0).fillColor(doodle.core.Color.blue)

  def testInverse[A](
      picture: Picture[Unit]
  )(implicit
      b: Base64[Algebra, Drawing, Frame, A],
      tp: ToPicture[Drawing, B64[A]]
  ) = {
    val (_, b1) = picture.base64[A](Frame.fitToPicture(0))
    val (_, b2) =
      b1.toPicture[Algebra, Drawing].base64[A](Frame.fitToPicture(0))
    val error = base64Distance(b1, b2)
    // Large threshold because the round-trip introduces a small vertical
    // displacement that ends up causing a large error. Not sure of the source
    // of this.
    val threshold = b1.value.length() * 64

    // if(error > threshold) {
    //   import cats.implicits._
    //   text[Algebra, Drawing]("badness").above(
    //     b1.toPicture[Algebra, Drawing].debug.beside(b2.toPicture[Algebra, Drawing].debug)
    //   ).draw()
    // }
    assert(error <= threshold, s"Error: ${error}\nThreshold: ${threshold}")
  }

  test("toPicture should work with png") {
    testInverse[Png](image)
  }

  test("toPicture should work with gif") {
    testInverse[Gif](image)
  }

  test("toPicture should work with jpg") {
    testInverse[Jpg](image)
  }
}
