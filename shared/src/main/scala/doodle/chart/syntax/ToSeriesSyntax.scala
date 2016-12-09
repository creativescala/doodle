package doodle
package chart
package syntax

trait ToSeriesSyntax {
  implicit class ToSeriesOps[A](a: A) {
    def toSeries(implicit p: ToSeries[A]): Series =
      p.toSeries(a)
  }
}
