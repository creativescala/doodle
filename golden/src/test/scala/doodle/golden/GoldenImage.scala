package doodle
package golden

import doodle.effect.Writer._
import doodle.java2d._
import munit._

import javax.imageio.ImageIO

trait GoldenImage extends Golden { self: FunSuite =>
  import doodle.image._
  import doodle.image.syntax._
  import doodle.syntax._

  def assertGoldenImage(name: String, image: Image)(implicit loc: Location) = {
    import java.io.File
    val file = new File(s"${goldenDir}/${name}.png")

    if (file.exists()) {
      val temp = new File(s"${goldenDir}/${name}.tmp.png")

      try {
        image.write[Png](temp)
        val actual = ImageIO.read(temp)
        val expected = ImageIO.read(file)

        assertEquals(
          actual.getHeight(),
          expected.getHeight(),
          s"Heights differ"
        )
        assertEquals(actual.getWidth(), expected.getWidth(), s"Widths differ")

        // Fairly arbitrary threshold allowing a 4-bit difference in each component of each pixel
        val threshold = actual.getHeight() * actual.getWidth() * 4 * 16
        val (error, diff) = absoluteError(actual, expected)
        val (_, diff64) = diff.toPicture[Algebra, Drawing].base64[Png]()

        assert(clue(error) < clue(threshold), diff64)
      } finally {
        if (temp.exists()) temp.delete()
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
