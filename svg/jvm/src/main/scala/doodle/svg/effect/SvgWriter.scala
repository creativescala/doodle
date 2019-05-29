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
  import Text.implicits._
  import Text.{svgTags => svg}
  import Text.svgAttrs

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
      (_, (tags, a)) = rdr.run(Transform.identity).value
      nodes = svg.svg(svgAttrs.width:=bb.width,
                      svgAttrs.height:=bb.height,
                      svgAttrs.viewBox:=s"${bb.left} ${bb.bottom} ${bb.width} ${bb.height}",
                      tags).render
      _ = Files.write(file.toPath, nodes.render.getBytes())
    } yield a
}
