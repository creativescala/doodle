package doodle
package chart
package syntax

trait AsSeriesSyntax {
  implicit class AsSeriesOps[A](a: A) {
    def asSeries(implicit p: AsSeries[A]): Series =
      p.asSeries(a)
  }
}
