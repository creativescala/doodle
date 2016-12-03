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

  final case class Data(points: List[Point]) extends Series
  final case class Legend(series: Series, title: String) extends Series
}
