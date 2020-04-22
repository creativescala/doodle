package doodle
package golden

import doodle.algebra.{Algebra, Picture}
import doodle.java2d._
import doodle.effect.Writer._
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import munit._
import doodle.effect.Writer

trait Golden { self: FunSuite =>
  val goldenDir = "golden/src/test/golden"

  def pixelAbsoluteError(a: Int, b: Int): Int = {
    var error = 0
    var i = 0
    while (i < 4) {
      val shift = i * 8
      val mask = 0x000000FF << shift
      val aValue = (a & mask) >> shift
      val bValue = (b & mask) >> shift

      error = error + Math.abs(aValue - bValue)

      i = i + 1
    }
    error
  }

  def absoluteError(
      actual: BufferedImage,
      golden: BufferedImage
  ): (Double, BufferedImage) = {
    val diff = new BufferedImage(
      actual.getWidth(),
      actual.getHeight(),
      BufferedImage.TYPE_INT_ARGB
    )
    // Sum of squared error
    var error = 0.0

    var x = 0
    while (x < actual.getWidth()) {
      var y = 0
      while (y < actual.getHeight()) {
        val pixelError = pixelAbsoluteError(
          actual.getRGB(x, y),
          golden.getRGB(x, y)
        )
        // Convert pixelError to black and white value for easier rendering
        val err =
          (256 * ((pixelError.toDouble) / (Int.MaxValue.toDouble))).toInt
        val pixel = (err << 16) | (err << 8) | err
        diff.setRGB(x, y, pixel)

        error = error + pixelError

        y = y + 1
      }
      x = x + 1
    }

    (error, diff)
  }
}

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

trait GoldenPicture extends Golden { self: FunSuite =>
  import doodle.syntax._

  def assertGoldenPicture[Alg[x[_]] <: Algebra[x], F[_]](
      name: String,
      picture: Picture[Alg, F, Unit],
      frame: Frame = Frame.fitToPicture()
  )(implicit loc: Location, w: Writer[Alg, F, Frame, Png]) = {
    import java.io.File
    val file = new File(s"${goldenDir}/${name}.png")

    if (file.exists()) {
      val temp = new File(s"${goldenDir}/${name}.tmp.png")

      try {
        picture.write[Png](temp, frame)
        val actual = ImageIO.read(temp)
        val expected = ImageIO.read(file)

        assertEquals(
          actual.getHeight(),
          expected.getHeight(),
          s"Heights differ"
        )
        assertEquals(actual.getWidth(), expected.getWidth(), s"Widths differ")

        // Fairly arbitrary threshold allowing a 4-bit difference in each pixel
        val threshold = actual.getHeight() * actual.getWidth() * 4 * 16 * 16
        val (error, diff) = absoluteError(actual, expected)
        val (_, diff64) = diff.toPicture[Algebra, Drawing].base64[Png]()

        assert(clue(error) < clue(threshold), diff64)
      } finally {
        if (temp.exists()) temp.delete()
        ()
      }
    } else {
      println(s"Golden: ${file} does not exist. Creating golden image.")
      picture.write[Png](file, frame)
    }
  }

  def testPicture[Alg[x[_]] <: Algebra[x], F[_], A](name: String)(
      picture: Picture[Alg, F, Unit]
  )(implicit loc: Location, w: Writer[Alg, F, Frame, Png]) =
    test(name) {
      assertGoldenPicture(name, picture)
    }

  def testPictureWithFrame[Alg[x[_]] <: Algebra[x], F[_], A](name: String)(frame: Frame)(
      picture: Picture[Alg, F, Unit]
  )(implicit loc: Location, w: Writer[Alg, F, Frame, Png]) =
    test(name) {
      assertGoldenPicture(name, picture, frame)
    }
}
