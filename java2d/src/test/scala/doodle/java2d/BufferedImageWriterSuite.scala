package doodle.java2d

import cats.effect.unsafe.implicits.global
import doodle.core.format.*
import doodle.syntax.all.*
import munit.CatsEffectSuite
import javax.imageio.ImageIO
import java.io.File

class BufferedImageWriteSuite extends CatsEffectSuite {
  val picture = Picture.circle(20.0)

  test(
    "writing to file produces the same output as writing BufferedImage to file"
  ) {
    val file = new File("buffered-image.png")

    for {
      _ <- picture.writeToIO[Png](file)
      _ = assert(file.exists())
      r <- picture.bufferedImageToIO()
      (_, bi) = r
      bi2 = ImageIO.read(file)
    } yield {
      assertEquals(bi.getWidth(), bi2.getWidth())
      assertEquals(bi.getHeight(), bi2.getHeight())

      assert(file.delete())
    }
  }
}
