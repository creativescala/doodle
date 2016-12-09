package doodle
package chart

import cats.Traverse
import doodle.core.Point
import scala.language.higherKinds

/**
  * Type class for converting `A` (typically a list of points)---something that can be turned into a data series in a plot.
  */
trait ToSeries[A] {
  def toSeries(in: A): Series
}
object ToSeries {
  implicit def traverseToPointTuplesToSeries[F[_]: Traverse, A: ToPoint]: ToSeries[F[A]] =
    new ToSeries[F[A]] {
      import doodle.chart.syntax._
      import cats.syntax.foldable._

      def toSeries(in: F[A]): Series = {
        val data =
          in.foldLeft(List.empty[Point]){ (accum, elt) =>
            elt.toPoint :: accum
          }.reverse

        Series.data(data)
      }
    }
}
