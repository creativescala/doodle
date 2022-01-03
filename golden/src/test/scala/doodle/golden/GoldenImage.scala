package doodle
package golden

import cats.effect.unsafe.implicits.global
import doodle.effect.Writer._
import doodle.java2d._
import munit._

trait GoldenImage extends Golden { self: FunSuite =>
  import doodle.image._
  import doodle.image.syntax.all._

  def assertGoldenImage(name: String, image: Image)(implicit loc: Location) = {
    import java.io.File
    val file = new File(s"${goldenDir}/${name}.png")

    if (file.exists()) {
      val temp = new File(s"${goldenDir}/${name}.tmp.png")

      try {
        // We must do these operations sequentially. If we used the `write`
        // syntax instead of the `writeToIO` syntax the writing occurs
        // asynchronously as may not finish before we attempt to calculate the
        // image diff.
        image
          .writeToIO[Png](temp)
          .map(_ => imageDiff(file, temp))
          .unsafeRunSync()
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
