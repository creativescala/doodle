package doodle
package svg
package effect

import cats.effect.IO
import doodle.core.{Base64 => B64}
import doodle.effect._
import doodle.algebra.Picture
import java.io.File
import java.nio.file.Files
import java.util.{Base64 => JBase64}

object SvgWriter
    extends Writer[Algebra, Drawing, Frame, Writer.Svg]
    with Base64[Algebra, Drawing, Frame, Writer.Svg] {
  def write[A](
      file: File,
      description: Frame,
      picture: Picture[Algebra, Drawing, A]
  ): IO[A] = {
    Svg
      .render[Algebra, A](description, algebraInstance, picture)
      .flatMap {
        case (nodes, a) =>
          IO {
            Files.write(file.toPath, nodes.getBytes())
            a
          }
      }
  }

  def write[A](file: File, picture: Picture[Algebra, Drawing, A]): IO[A] =
    write(file, Frame("").fitToPicture(), picture)

  def base64[A](frame: Frame,
                image: Picture[Algebra, Drawing, A]): IO[(A, B64[Writer.Svg])] =
    for {
      rendered <- Svg
        .render[Algebra, A](frame, algebraInstance, image)
      (nodes, value) = rendered
      b64 = JBase64.getEncoder.encodeToString(nodes.getBytes())
    } yield (value, B64[Writer.Svg](b64))

  def base64[A](
      picture: Picture[Algebra, Drawing, A]): IO[(A, B64[Writer.Svg])] =
    base64(Frame("").fitToPicture(), picture)
}
