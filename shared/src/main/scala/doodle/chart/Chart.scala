package doodle
package chart

/** The different types of chart we know how to draw */
sealed abstract class ChartType extends Product with Serializable
object ChartType {
  final case object ScatterPlot extends ChartType
}

/**
  * The representation of a Chart.
  *
  * A Chart doesn't have interesting recursive structure so we can use just a product type to represent it.
  */
final case class Chart(chartType: ChartType, series: Series, title: Option[String]) {
  def title(title: String): Chart =
    this.copy(title = Some(title))
}
object Chart {
  // Constructors --------------------------------------------------------------

  def scatterPlot(series: Series): Chart =
    Chart(ChartType.ScatterPlot, series, title = None)
}
