package doodle
package java2d

import doodle.core.Base64
import doodle.effect.Writer._
import doodle.syntax._
import minitest._

object Base64Spec extends SimpleTestSuite {
  def base64Distance[A](b1: Base64[A], b2: Base64[A]): Double = {
    import java.util.{Base64 => JBase64}
    val d1 = JBase64.getDecoder().decode(b1.value)
    val d2 = JBase64.getDecoder().decode(b2.value)

    d1.zip(d2).foldLeft(0.0) { (accum, elts) =>
      val (byte1, byte2) = elts
      accum + Math.abs(byte1 - byte2)
    }
  }

  val image = circle[Algebra, Drawing](20.0)

  test("base64 should work with png") {
    val (_, b1) = image.base64[Png]()
    val (_, b2) = image.base64[Png]()

    assert(base64Distance(b1, b2) <= (b1.value.length * 2))
  }

  test("base64 should work with gif") {
    val (_, b1) = image.base64[Gif]()
    val (_, b2) = image.base64[Gif]()

    assert(base64Distance(b1, b2) <= (b1.value.length * 2))
  }

  test("base64 should work with jpg") {
    val (_, b1) = image.base64[Jpg]()
    val (_, b2) = image.base64[Jpg]()

    assert(base64Distance(b1, b2) <= (b1.value.length * 2))
  }
}
