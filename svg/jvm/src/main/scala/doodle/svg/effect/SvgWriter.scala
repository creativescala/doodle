package doodle
package svg
package effect

import cats.effect.IO
import doodle.core.Transform
import doodle.effect._
import doodle.algebra.Picture
import java.io.File
import java.nio.file.Files
import scalatags.Text

object SvgWriter extends Writer[Algebra, Drawing, SvgFrame, Writer.Svg] {
  val algebra = doodle.svg.algebra.Algebra(Text)

  def write[A](file: File,
               description: SvgFrame,
               picture: Picture[Algebra,Drawing,A]): IO[A] = {
    // Frame is (currently) meaningless for writing
    val _ = description
    write(file, picture)
  }

  def write[A](file: File,
               picture: Picture[Algebra,Drawing,A]): IO[A] =
    for {
      drawing <- IO { picture(algebra) }
      (bb, rdr) = drawing.runA(List.empty).value
      (_, (txt, a)) = rdr.run(Transform.identity).value
      _ = Files.write(file.toPath, txt.render.getBytes())
    } yield a
}
