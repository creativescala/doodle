package doodle
package svg
package effect

import cats.effect.IO
import doodle.core.Transform
import doodle.effect.Renderer
import scalatags.JsDom

object SvgRenderer extends Renderer[Algebra, Drawing, Frame, Canvas] {
  val algebra = doodle.svg.algebra.Algebra(JsDom)

  def frame(description: Frame): IO[Canvas] =
    IO{ Canvas.fromFrame(description) }

  def render[A](canvas: Canvas)(image: Picture[A]): IO[A] =
    for {
      drawing <- IO { image(algebra) }
      (bb, rdr) = drawing.runA(List.empty).value
      (_, (tags, a)) = rdr.run(Transform.identity).value
      nodes = tags.render
      _ = canvas.setSvg(nodes)
    } yield a

}
