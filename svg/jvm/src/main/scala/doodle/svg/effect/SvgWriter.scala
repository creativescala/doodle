package doodle
package svg
package effect

import cats.effect.IO
import doodle.effect._
import doodle.algebra.Picture
import java.io.File
import java.nio.file.Files
import scalatags.Text

object SvgWriter extends Writer[Algebra, Drawing, Frame, Writer.Svg] {
  val algebra = doodle.svg.algebra.Algebra(Text)
  val svg = Svg(Text)

  def write[A](file: File,
               description: Frame,
               picture: Picture[Algebra, Drawing, A]): IO[A] = {
    // Frame is (currently) meaningless for writing
    val _ = description
    write(file, picture)
  }

  def write[A](file: File, picture: Picture[Algebra, Drawing, A]): IO[A] =
    svg
      .render[Algebra, A](Frame("").fitToPicture(), algebra, picture)
      .flatMap {
        case (nodes, a) =>
          IO {
            Files.write(file.toPath, nodes.getBytes())
            a
          }
      }
}
