package doodle
package golden

import cats.effect.unsafe.implicits.global
import doodle.algebra.Algebra
import doodle.algebra.Picture
import doodle.core.format._
import doodle.effect.Writer
import doodle.java2d._
import munit._

trait GoldenPicture extends Golden { self: FunSuite =>
  import doodle.syntax.all._

  def assertGoldenPicture[Alg <: Algebra, F[_]](
      name: String,
      picture: Picture[Alg, Unit],
      frame: Frame = Frame.fitToPicture()
  )(implicit loc: Location, w: Writer[Alg, Frame, Png]) = {
    import java.io.File
    val file = new File(s"${goldenDir}/${name}.png")

    if (file.exists()) {
      val temp = new File(s"${goldenDir}/${name}.tmp.png")

      try {
        picture.write[Png](temp, frame)

        imageDiff(file, temp)
      } finally {
        if (temp.exists()) temp.delete()
        ()
      }
    } else {
      println(s"Golden: ${file} does not exist. Creating golden image.")
      picture.write[Png](file, frame)
    }
  }

  def testPicture[Alg <: Algebra, F[_], A](name: String)(
      picture: Picture[Alg, Unit]
  )(implicit loc: Location, w: Writer[Alg, Frame, Png]) =
    test(name) {
      assertGoldenPicture(name, picture)
    }

  def testPictureWithFrame[Alg <: Algebra, F[_], A](name: String)(
      frame: Frame
  )(
      picture: Picture[Alg, Unit]
  )(implicit loc: Location, w: Writer[Alg, Frame, Png]) =
    test(name) {
      assertGoldenPicture(name, picture, frame)
    }
}
