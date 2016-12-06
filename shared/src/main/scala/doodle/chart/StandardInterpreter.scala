package doodle
package chart

import doodle.core.{Color, Image, Vec}
import doodle.syntax._

object StandardInterpreter {
  // Linear interpolate from data space to screen space
  // Hard coded width and height of 600 for now
  def lerp(min: Double, max: Double): (Double => Double, Double => Double) =
    (
      (data: Double) => ((data - min) / (max - min)) * 600,
      (screen: Double) => (screen / 600) * (max - min) + min
    )

  def seriesToImage(series: Series): Image = {
    val min = series.min
    val max = series.max
    val padding = 0.05
    val xLength = (max.x - min.x) + (2 * padding)
    val yLength = (max.y - min.y) + (2 * padding)

    val (xLerp, xReverse) = lerp(min.x - (xLength * padding), max.x + (xLength * padding))
    val (yLerp, yReverse) = lerp(min.y - (yLength * padding), max.y + (yLength * padding))

    val alpha = 0.8.normalized
    val line = Color.navy.alpha(alpha)
    val fill = Color.royalBlue.alpha(alpha)
    val pts = series.data.foldLeft(Image.empty){ (accum, pt) =>
      val marker = (Image.circle(5).lineColor(line).fillColor(fill))
      val loc = Vec(xLerp(pt.x), yLerp(pt.y))
      (marker at loc) on accum
    }

    val axisPadding = (xLength max yLength) * padding
    val xAxis = Image.horizontalLine(600) at (Vec.zero)
    val yAxis = Image.verticalLine(600) at (Vec.zero)

    val xTick = Image.verticalLine(5)
    val yTick = Image.horizontalLine(5)
    val ticks = (100 to 600 by 100).foldLeft(Image.empty){ (accum, i) =>
      val xLabel = (Image.text("%1.2f".format(xReverse(i)))) at Vec(i, -10)
      val yLabel = (Image.text("%1.2f".format(yReverse(i)))) at Vec(-20, i)
      (xTick at Vec(i, 0)) on xLabel on (yTick at Vec(0, i)) on yLabel on accum
    }

    val axes = xAxis on yAxis on ticks

    pts on axes
  }

  implicit val chartInterpreter: Interpreter = {
    case Chart(chartType, series, title) => {
      chartType match {
        case ChartType.ScatterPlot =>
          title.map(t => Image.text(t)).getOrElse(Image.empty) above (seriesToImage(series))
      }
    }
  }
}
