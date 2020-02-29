package doodle
package golden

import doodle.algebra.{Algebra, Picture}
import doodle.image._
import doodle.image.syntax._
import doodle.java2d._
import doodle.effect.Writer._
import doodle.syntax._
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import munit._
import doodle.effect.Writer

trait Golden { self: FunSuite =>
  val goldenDir = "golden/src/test/golden"

  def pixelSumOfSquaredError(a: Int, b: Int): Int = {
    var error = 0
    var i = 0
    while(i < 4) {
      val shift = i * 8
      val mask = 0x000000FF << shift
      val aValue = (a & mask) >> shift
      val bValue = (b & mask) >> shift

      error = error + ((aValue - bValue) * (aValue - bValue))

      i = i + 1
    }
    error
  }

  def sumOfSquaredError(actual: BufferedImage, golden: BufferedImage): Double = {
    // Sum of squared error
    var error = 0.0

    var x = 0
    while(x < actual.getWidth()) {
      var y = 0
      while(y < actual.getHeight()) {
        error = error + pixelSumOfSquaredError(actual.getRGB(x, y), golden.getRGB(x, y))

        y = y + 1
      }
      x = x + 1
    }

    error
  }

  def assertGoldenImage(name: String, image: Image)(implicit loc: Location) = {
    import java.io.File
    val file = new File(s"${goldenDir}/${name}.png")

    if(file.exists()) {
      val temp = new File(s"${goldenDir}/${name}.tmp.png")

      try {
        image.write[Png](temp)
        val actual = ImageIO.read(temp)
        val expected = ImageIO.read(file)

        assertEquals(actual.getHeight(), expected.getHeight(), s"Heights differ")
        assertEquals(actual.getWidth(), expected.getWidth(), s"Widths differ")

        // Fairly arbitrary threshold allowing a 4-bit difference in each pixel
        val threshold = actual.getHeight() * actual.getWidth() * 4 * 16 * 16
        val error = sumOfSquaredError(actual, expected)

        assert(clue(error) < clue(threshold), "Error greater than threshold")
      } finally {
        if(temp.exists()) temp.delete()
        ()
      }
    } else {
      println(s"Golden: ${file} does not exist. Creating golden image.")
      image.write[Png](file)
    }
  }

  def assertGoldenPicture[Alg[x[_]] <: Algebra[x], F[_]](name: String, picture: Picture[Alg, F, Unit])(implicit loc: Location, w: Writer[Alg, F, Frame, Png]) = {
    import java.io.File
    val file = new File(s"${goldenDir}/${name}.png")

    if(file.exists()) {
      val temp = new File(s"${goldenDir}/${name}.tmp.png")

      try {
        picture.write[Png](temp)
        val actual = ImageIO.read(temp)
        val expected = ImageIO.read(file)

        assertEquals(actual.getHeight(), expected.getHeight(), s"Heights differ")
        assertEquals(actual.getWidth(), expected.getWidth(), s"Widths differ")

        // Fairly arbitrary threshold allowing a 4-bit difference in each pixel
        val threshold = actual.getHeight() * actual.getWidth() * 4 * 16 * 16
        val error = sumOfSquaredError(actual, expected)

        assert(clue(error) < clue(threshold), "Error greater than threshold")
      } finally {
        if(temp.exists()) temp.delete()
        ()
      }
    } else {
      println(s"Golden: ${file} does not exist. Creating golden image.")
      picture.write[Png](file)
    }
  }

  def testImage(name: String)(image: Image)(implicit loc: Location) =
    test(name) {
      assertGoldenImage(name, image)
    }

  def testPicture[Alg[x[_]] <: Algebra[x], F[_], A](name: String)(picture: Picture[Alg, F, Unit])(implicit loc: Location, w: Writer[Alg, F, Frame, Png]) =
    test(name) {
      assertGoldenPicture(name, picture)
    }
}
