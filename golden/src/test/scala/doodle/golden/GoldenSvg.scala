package doodle
package golden

import doodle.algebra.{Algebra, Picture}
import doodle.svg._
import java.io.{FileOutputStream, StringReader}
import munit._
import org.apache.batik.transcoder.{TranscoderInput, TranscoderOutput}
import org.apache.batik.transcoder.image.PNGTranscoder

trait GoldenSvg extends Golden { self: FunSuite =>

  def assertGoldenPicture[Alg[x[_]] <: Algebra[x]](
      name: String,
      picture: Picture[Alg, Drawing, Unit],
      frame: Frame = Frame.fitToPicture()
  )(implicit loc: Location) = {
    import java.io.File
    val file = new File(s"${goldenDir}/svg/${name}.png")

    if (file.exists()) {
      val temp = new File(s"${goldenDir}/svg/${name}.tmp.png")

      try {
        Svg.render(frame, algebraInstance, picture)
          .map{ case (str, _) =>
            val transcoder = new PNGTranscoder()
            val input = new TranscoderInput(new StringReader(str))
            val os = new FileOutputStream(temp)
            val output = new TranscoderOutput(os)

            transcoder.transcode(input, output)
            os.flush()
            os.close()
          }
          .unsafeRunSync()

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

}
