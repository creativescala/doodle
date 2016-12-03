package doodle
package chart

import cats.Traverse
import doodle.core.Point
import scala.language.higherKinds

/**
  * Type class for converting `A` (typically a list of points)---something that can be turned into a data series in a plot.
  */
trait AsSeries[A] {
  def asSeries(in: A): Series
}
object AsSeries {
  implicit def seqAsPointTuplesToSeries[F[_]: Traverse, A: AsPoint]: AsSeries[F[A]] =
    new AsSeries[F[A]] {
      import doodle.chart.syntax._
      import cats.syntax.foldable._

      def asSeries(in: F[A]): Series = {
        val data =
          in.foldLeft(List.empty[Point]){ (accum, elt) =>
            elt.asPoint :: accum
          }.reverse

        Series.data(data)
      }
    }
}
