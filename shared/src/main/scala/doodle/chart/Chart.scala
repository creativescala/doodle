package doodle
package chart

/**
  * The algebraic data type representing our Chart DSL
  */
sealed abstract class Chart extends Product with Serializable {
  import Chart._

  def title(title: String): Chart =
    Title(this, title)
}
object Chart {
  def scatterPlot(series: Series): Chart =
    ScatterPlot(series)

  final case class Title(chart: Chart, title: String) extends Chart
  final case class ScatterPlot(series: Series) extends Chart
}
