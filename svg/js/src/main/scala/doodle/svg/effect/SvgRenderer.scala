package doodle
package svg
package effect

import cats.effect.IO
import doodle.effect.Renderer

object SvgRenderer extends Renderer[Algebra, Drawing, Frame, Canvas] {
  def canvas(description: Frame): IO[Canvas] =
    IO{ Canvas.fromFrame(description) }

  def render[A](canvas: Canvas)(picture: Picture[A]): IO[A] =
    canvas.render(picture)
}
