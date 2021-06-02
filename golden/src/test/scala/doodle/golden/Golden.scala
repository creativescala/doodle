package doodle
package golden

import java.awt.image.BufferedImage
import munit._

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
