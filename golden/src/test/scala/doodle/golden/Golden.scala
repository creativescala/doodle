package doodle
package golden

import doodle.image._
import doodle.image.syntax._
import doodle.java2d._
import doodle.effect.Writer._
import javax.imageio.ImageIO
import munit._

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

        // Sum of squared error
        var error = 0.0
        val threshold = actual.getHeight() * actual.getWidth() * 4

        var x = 0
        while(x < actual.getWidth()) {
          var y = 0
          while(y < actual.getHeight()) {
            error = error + pixelSumOfSquaredError(actual.getRGB(x, y), expected.getRGB(x, y))

            y = y + 1
          }
          x = x + 1
        }

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

  def testImage(name: String)(image: Image)(implicit loc: Location) =
    test(name) {
      assertGoldenImage(name, image)
    }
}
