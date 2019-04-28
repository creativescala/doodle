package doodle
package svg
package effect

import cats.effect.IO
import doodle.core.Transform
import doodle.effect._
import doodle.algebra.Image
import doodle.svg.algebra.Algebra
import java.io.File
import java.nio.file.Files
import scalatags.Text

object SvgWriter extends Writer[Algebra.type, Drawing, SvgFrame, Writer.Svg] {
  val svg = Svg(Text)

  def write[A, Alg >: Algebra.type](file: File,
                               description: SvgFrame,
                               image: Image[Alg,Drawing,A]): IO[A] = {
    // Frame is (currently) meaningless for writing
    val _ = description
    write(file, image)
  }

  def write[A, Alg >: Algebra.type](file: File,
                               image: Image[Alg,Drawing,A]): IO[A] =
    for {
      drawing  <- IO { image(Algebra) }
      (bb, rdr) = drawing.runA(List.empty).value
      (tx, fa)  = rdr.run(Transform.identity).value
      (r, a)    = fa.run
      text      = svg.render(bb, r).render
      _         = Files.write(file.toPath, text.getBytes())
    } yield a

}
