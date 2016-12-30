package doodle
package chart
package interpreter

import cats.data.Reader
import doodle.core.{Color,Image,Point,Vec}
import doodle.syntax._
import scala.language.higherKinds

object Series {
  import chart.Series._

  // For the parameter `numerical` we really want to express `Numerical[F[_]]`
  // where `F` is an existential. Scala does not support higher kinded
  // existentials, so we have to erase to `Any` instead.
  def numerical[F[_]](numerical: Numerical[F]): Reader[Style,Image] = {
    Reader(style =>
      numerical match {
        case Numerical(c, d, t, l) =>
          val min = numerical.min
          val max = numerical.max
          val alpha = 0.8.normalized
          val line = Color.navy.alpha(alpha)
          val fill = Color.royalBlue.alpha(alpha)
          val marker = (Image.circle(5).lineColor(line).fillColor(fill))

          val pad = style.padding
          val dataToScreenX =
            Interpolation.linear(min.x, max.x - min.x, pad, style.width - (2 * pad))
          val dataToScreenY =
            Interpolation.linear(min.y, max.y - min.y, pad, style.height - (2 * pad))

          t.foldLeft(d, Image.empty){ (img, pt: Point) =>
            val loc = Vec(dataToScreenX(pt.x), dataToScreenY(pt.y))
            (marker at loc) on img
          }
      }
    )
  }
}
