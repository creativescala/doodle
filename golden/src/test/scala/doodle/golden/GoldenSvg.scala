package doodle
package golden

import doodle.algebra.Picture
import doodle.language.Basic
import doodle.svg._
import java.io.{File, FileOutputStream, StringReader}
import munit._
import org.apache.batik.transcoder.{TranscoderInput, TranscoderOutput}
import org.apache.batik.transcoder.image.PNGTranscoder

trait GoldenSvg extends Golden { self: FunSuite =>

  def writeToPng(frame: Frame, picture: Picture[Basic, Drawing, Unit], file: File): Unit = {
    Svg.render(frame, algebraInstance, picture)
      .map{ case (str, _) =>
        val transcoder = new PNGTranscoder()
        val input = new TranscoderInput(new StringReader(str))
        val os = new FileOutputStream(file)
        val output = new TranscoderOutput(os)

        transcoder.transcode(input, output)
        os.flush()
        os.close()
      }
      .unsafeRunSync()
  }

  def assertGoldenPicture(
      name: String,
      picture: Picture[Basic, doodle.svg.jvm.Drawing, Unit],
      frame: Frame = Frame("").fitToPicture()
  )(implicit loc: Location) = {
    import java.io.File
    val file = new File(s"${goldenDir}/svg/${name}.png")

    if (file.exists()) {
      val temp = new File(s"${goldenDir}/svg/${name}.tmp.png")
      writeToPng(frame, picture, temp)

      try {
        imageDiff(file, temp)
      } finally {
        if (temp.exists()) temp.delete()
        ()
      }
    } else {
      println(s"Golden: ${file} does not exist. Creating golden image.")
      writeToPng(frame, picture, file)
    }
  }

}
