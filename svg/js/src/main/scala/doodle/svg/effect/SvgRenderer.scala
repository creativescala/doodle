package doodle
package svg
package effect

import cats.effect.IO
import doodle.core.Transform
import doodle.effect.Renderer
import doodle.svg.algebra.Algebra
import scalatags.JsDom

object SvgRenderer extends Renderer[Algebra, Drawing, SvgFrame, SvgCanvas] {
  val svg = Svg(JsDom)

  def frame(description: SvgFrame): IO[SvgCanvas] =
    ???

  def render[A](canvas: SvgCanvas)(f: Algebra => Drawing[A]): IO[A] =
    for {
      drawing <- IO { f(Algebra) }
      (bb, rdr) = drawing.runA(List.empty).value
      (r, _, a) = rdr.run((), Transform.identity).value
      nodes = svg.render(bb, r).render
      _ = canvas.setSvg(nodes)
    } yield a

}
