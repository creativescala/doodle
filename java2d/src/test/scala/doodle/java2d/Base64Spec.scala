package doodle
package java2d

import doodle.effect.Writer._
import doodle.syntax._
import minitest._

object Base64Spec extends SimpleTestSuite {
  val image = circle[Algebra,Drawing](20.0)

  test("write should work with png") {
    val (_, string1) = image.base64[Png]()
    val (_, string2) = image.base64[Png]()

    assertEquals(string1, string2)
  }

  test("write should work with gif") {
    val (_, string1) = image.base64[Gif]()
    val (_, string2) = image.base64[Gif]()

    assertEquals(string1, string2)
  }

  test("write should work with pdf") {
    val (_, string1) = image.base64[Pdf]()
    val (_, string2) = image.base64[Pdf]()

    assertEquals(string1, string2)
  }

  test("write should work with jpg") {
    val (_, string1) = image.base64[Jpg]()
    val (_, string2) = image.base64[Jpg]()

    assertEquals(string1, string2)
  }
}
