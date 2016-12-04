package doodle
package chart

import doodle.core.{Color, Image, PathElement, Vec}
import doodle.syntax._

object StandardInterpreter {
  import Series._
  import Chart._

  final case class FinalisedSeries(legend: Option[String], data: Data)
  object FinalisedSeries {
    def fromSeries(series: Series): FinalisedSeries = {
      def iter(series: Series, legend: Option[String] = None): FinalisedSeries =
        series match {
          case Legend(s, l) =>
            iter(s, legend orElse Some(l))

          case d @ Data(pts) =>
            FinalisedSeries(legend, d)
        }

      iter(series)
    }
  }

  def seriesToImage(series: Series): Image = {
    val finalised = FinalisedSeries.fromSeries(series)
    val min = finalised.data.min
    val max = finalised.data.max


    val alpha = 0.8.normalized
    val line = Color.navy.alpha(alpha)
    val fill = Color.royalBlue.alpha(alpha)
    val pts = finalised.data.points.foldLeft(Image.empty){ (accum, pt) =>
      (Image.circle(5).lineColor(line).fillColor(fill).at(pt.toVec)) on accum
    }

    val axisPadding = ((max.x - min.x) max (max.y - min.y)) * 0.05
    val xAxis =
      Image.openPath(Seq(PathElement.lineTo(max.x - min.x + (2*axisPadding), 0.0))) at (Vec(min.x - axisPadding, 0.0))
    val yAxis =
      Image.openPath(Seq(PathElement.lineTo(0.0, max.y - min.y + (2*axisPadding)))) at (Vec(0.0, min.y - axisPadding))

    val axes = xAxis on yAxis

    pts on axes
  }

  implicit val chartInterpreter: Interpreter =
    (chart: Chart) => {
      def loop(chart: Chart, title: Option[String] = None): Image = {
        chart match {
          case Title(c, t) =>
            // Choose outermost title
            loop(c, title orElse Some(t))

          case ScatterPlot(series) =>
            title.map(t => Image.text(t)).getOrElse(Image.empty) above (seriesToImage(series))
        }
      }

      loop(chart)
    }
}
