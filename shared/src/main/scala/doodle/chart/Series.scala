package doodle
package chart

import doodle.core.Point

/**
  * A `Series` represents a group of data that should be drawn on the chart.
  */
final case class Series(data: List[Point], legend: Option[String]) {
  def legend(legend: String): Series =
    this.copy(legend = Some(legend))

  val min: Point =
    data.fold(Point.zero){ (min, elt) =>
      Point(min.x min elt.x, min.y min elt.y)
    }
  val max: Point =
    data.fold(Point.zero){ (max, elt) =>
      Point(max.x max elt.x, max.y max elt.y)
    }
}
object Series {
  def data(points: List[Point]): Series =
    Series(points, None)
}
