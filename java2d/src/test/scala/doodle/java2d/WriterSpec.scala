package doodle
package java2d

import cats.effect.unsafe.implicits.global
import doodle.effect.Writer._
import doodle.syntax._
import minitest._

import java.io.File

object WriterSpec extends SimpleTestSuite {
  val image = circle[Algebra, Drawing](20.0)

  test("write should work with png") {
    image.write[Png]("circle.png")
    val output = new File("circle.png")
    assertEquals(output.exists(), true)
    assertEquals(output.delete(), true)
  }

  test("write should work with gif") {
    image.write[Gif]("circle.gif")
    val output = new File("circle.gif")
    assertEquals(output.exists(), true)
    assertEquals(output.delete(), true)
  }

  test("write should work with pdf") {
    image.write[Pdf]("circle.pdf")
    val output = new File("circle.pdf")
    assertEquals(output.exists(), true)
    assertEquals(output.delete(), true)
  }

  test("write should work with jpg") {
    image.write[Jpg]("circle.jpg")
    val output = new File("circle.jpg")
    assertEquals(output.exists(), true)
    assertEquals(output.delete(), true)
  }
}
