package doodle
package svg
package effect

import cats.effect.IO
import doodle.effect.Renderer
import scalatags.JsDom

object SvgRenderer extends Renderer[Algebra, Drawing, Frame, Canvas] {
  val algebra = doodle.svg.algebra.Algebra
  val svg = Svg(JsDom)

  def canvas(description: Frame): IO[Canvas] =
    IO{ Canvas.fromFrame(description) }

  def render[A](canvas: Canvas)(picture: Picture[A]): IO[A] =
    svg.render[Algebra,A](canvas.size, algebra, picture)
      .flatMap{ case (nodes, a) => IO{ canvas.setSvg(nodes); a } }
}
