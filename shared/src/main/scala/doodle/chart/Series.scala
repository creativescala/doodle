package doodle
package chart

import doodle.core.Point

/**
  * A `Series` represents a group of data that should be drawn on the chart.
  */
sealed abstract class Series extends Product with Serializable {
  import Series._

  def legend(title: String): Series =
    Legend(this, title)
}
object Series {
  def data(points: List[Point]): Series =
    Data(points)

  final case class Data(points: List[Point]) extends Series {
    val min: Point =
      points.fold(Point.zero){ (min, elt) =>
        Point(min.x min elt.x, min.y min elt.y)
      }
    val max: Point =
      points.fold(Point.zero){ (max, elt) =>
        Point(max.x max elt.x, max.y max elt.y)
      }
  }
  final case class Legend(series: Series, title: String) extends Series 
}
