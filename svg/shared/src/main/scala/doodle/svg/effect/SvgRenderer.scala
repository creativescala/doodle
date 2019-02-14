package doodle
package svg
package effect

import cats.effect.IO
import doodle.effect.{Frame, Renderer}

object SvgRenderer extends  Renderer[Algebra, Drawing, SvgFrame] {
  def frame(description: Frame): IO[SvgFrame] =
    IO { ??? }

  def render[A](canvas: SvgFrame)(f: Algebra => Drawing[A]): IO[A] =
    ???
}
