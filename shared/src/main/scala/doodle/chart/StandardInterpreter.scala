package doodle
package chart

import doodle.core.{Color, Image}

object StandardInterpreter {
  import Series._
  import Chart._

  def seriesToImage(series: Series, legend: Option[String] = None): Image =
    series match {
      case Legend(s, l) =>
        seriesToImage(s, legend orElse Some(l))

      case Data(pts) =>
        pts.foldLeft(Image.empty){ (accum, pt) =>
          (Image.circle(5).fillColor(Color.royalBlue).at(pt.toVec)) on accum
        }
    }

  implicit val interpreter: Interpreter =
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
