package doodle
package golden

import doodle.syntax._
import doodle.image._
import doodle.image.syntax._
import doodle.java2d._
import doodle.effect.Writer._
import munit._

trait Golden { self: FunSuite =>
  val goldenDir = "golden/src/test/golden"

  def assertGoldenImage(name: String, image: Image)(implicit loc: Location) = {
    import java.io.File
    val file = new File(s"${goldenDir}/${name}.png")

    if(file.exists()) {
      val temp = new File(s"${goldenDir}/${name}.tmp.png")

      try {
        image.write[Png](temp)
        val (_, actual) = read[Algebra,Drawing](temp).base64[Png]()
        val (_, expected) = read[Algebra,Drawing](file).base64[Png]()

        assertEquals(actual, expected)
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
