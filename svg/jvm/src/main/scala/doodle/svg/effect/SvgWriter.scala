package doodle
package svg
package effect

import cats.effect.IO
import doodle.core.Transform
import doodle.effect._
import doodle.algebra.Picture
import doodle.svg.algebra.Algebra
import java.io.File
import java.nio.file.Files
import scalatags.Text

object SvgWriter extends Writer[doodle.svg.Algebra, Drawing, SvgFrame, Writer.Svg] {
  val svg = Svg(Text)

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
      drawing  <- IO { picture(Algebra) }
      (bb, rdr) = drawing.runA(List.empty).value
      (tx, fa)  = rdr.run(Transform.identity).value
      (r, a)    = fa.run.value
      text      = svg.render(bb, r).render
      _         = Files.write(file.toPath, text.getBytes())
    } yield a
}
