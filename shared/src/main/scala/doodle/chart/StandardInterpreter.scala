package doodle
package chart

import doodle.core.{Color, Image, PathElement, Vec}
import doodle.syntax._

object StandardInterpreter {
  def seriesToImage(series: Series): Image = {
    val min = series.min
    val max = series.max

    val alpha = 0.8.normalized
    val line = Color.navy.alpha(alpha)
    val fill = Color.royalBlue.alpha(alpha)
    val pts = series.data.foldLeft(Image.empty){ (accum, pt) =>
      (Image.circle(5).lineColor(line).fillColor(fill).at(pt.toVec)) on accum
    }

    val axes = axesToImage(series, Axes.linear)
    pts on axes
  }

  def axesToImage(series: Series, axes: Axes, padding: Double = 0.05): Image =
    axes match {
      case Axes.Linear =>
        val max = series.max
        val min = series.min
        val xLength = (max.x - min.x) + (2 * padding)
        val yLength = (max.y - min.y) + (2 * padding)
        val axisPadding = (xLength max yLength) * padding
        val xAxis =
          Image.openPath(Seq(PathElement.lineTo(xLength, 0.0))) at (Vec(min.x - axisPadding, 0.0))
        val yAxis =
          Image.openPath(Seq(PathElement.lineTo(0.0, yLength))) at (Vec(0.0, min.y - axisPadding))

        val xTickOffset = xLength/6
        val yTickOffset = yLength/6
        var xTicks = Image.empty
        var yTicks = Image.empty
        val xTick = Image.openPath(Seq(PathElement.lineTo(0, 5)))
        val yTick = Image.openPath(Seq(PathElement.lineTo(5, 0)))
        for(i <- 0 to 3) {
          xTicks = (xTick at Vec(xTickOffset * i, 0)) on (xTick at Vec(xTickOffset * -i, 0)) on xTicks
          yTicks = (yTick at Vec(0, yTickOffset * i)) on (yTick at Vec(0, yTickOffset * -i)) on yTicks
        }

        xAxis on yAxis on xTicks on yTicks
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
