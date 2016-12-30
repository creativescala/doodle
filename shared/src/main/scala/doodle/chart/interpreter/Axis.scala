package doodle
package chart
package interpreter

import cats.data.Reader
import doodle.core.{Image,Vec}

object Axis {
  def numerical(
    axis: chart.Axis.Numerical,
    direction: chart.Axis.Direction,
    min: Double,
    max: Double
  ): Reader[Style,Image] = {
    import doodle.chart.Axis._
    Reader(style =>
      axis match {
        case Numerical(l, Ticker.Parsimonious, Scale.Linear) =>
          val length = direction match {
            case Horizontal => style.width
            case Vertical => style.height
          }
          // Linear interpolation hardcoded for now
          val (dataToScreen, screenToData) = {
            val pad = style.padding
            (Interpolation.linear(min, max - min, pad, length - (2 * pad)),
             Interpolation.linear(pad, length - (2 * pad), min, max - min))
          }
          val ticks =
            (length/6 to length by length/6).foldLeft(Image.empty){ (accum, i) =>
              val label = (Image.text("%1.2f".format(screenToData(i))))

              direction match {
                case Horizontal =>
                  val tick = Image.verticalLine(5)
                  (tick above label) at Vec(i, 0) on accum
                case Vertical =>
                  val tick = Image.horizontalLine(5)
                  (label beside tick) at Vec(0, i) on accum
              }
            }
          val axis =
            direction match {
              case Horizontal => Image.horizontalLine(length)
              case Vertical => Image.verticalLine(length)
            }
          ticks on axis
      }
    )
  }
}
