package doodle
package golden

import doodle.effect.Writer._
import doodle.java2d._
import munit._

trait GoldenImage extends Golden { self: FunSuite =>
  import doodle.image._
  import doodle.image.syntax._

  def assertGoldenImage(name: String, image: Image)(implicit loc: Location) = {
    import java.io.File
    val file = new File(s"${goldenDir}/${name}.png")

    if (file.exists()) {
      val temp = new File(s"${goldenDir}/${name}.tmp.png")

      try {
        image.write[Png](temp)
        imageDiff(file, temp)
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
