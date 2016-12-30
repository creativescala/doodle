package doodle
package chart

import doodle.core.Point
import cats.Traverse
import scala.language.higherKinds

/**
  * A `Chart` can have many `Series`. For example, a climate chart displaying maximum, minimum, and average temperature. To render all `Series` on a single chart they must have the same domain and codomain (aka range). In the climate example this means all the `Series` plot date (domain) against temperature (codomain or range).
  *
  * `Series` is currently an algebraic data type, which limits extensibility. Changing this representation, perhaps to a type class, is an enhancement to consider.
  */
object Series {
  import cats.syntax.functor._
  import doodle.chart.syntax.toPoint._

  // Smart constructors ----------------------------------------------------

  def scatterChart[F[_],A](data: F[A])(implicit t: Traverse[F], p: ToPoint[A]): Numerical[F] =
    Numerical(NumericalChartType.Scatter, data.map(_.toPoint), t, None)


  // Series algebraic data type --------------------------------------------

  // For now we have a fixed number of chart types
  sealed abstract class NumericalChartType extends Product with Serializable
  object NumericalChartType {
    val scatter: NumericalChartType = Scatter

    final case object Scatter extends NumericalChartType
  }

  final case class Numerical[F[_]](
    chartType: NumericalChartType,
    data: F[Point],
    traverse: Traverse[F],
    legend: Option[String]
  ) {
    val min: Point =
      traverse.foldLeft(data, Point.max){ (min, pt: Point) =>
        Point(min.x min pt.x, min.y min pt.y)
      }

    val max: Point =
      traverse.foldLeft(data, Point.min){ (max, pt: Point) =>
        Point(max.x max pt.x, max.y max pt.y)
      }

    def legend(legend: String): Numerical[F] =
      this.copy(legend = Some(legend))
  }

  sealed abstract class CategoricalChartType extends Product with Serializable
  object CategoricalChartType {
    val scatter: CategoricalChartType = Bar

    final case object Bar extends CategoricalChartType
  }

  final case class Categorical[F[_],A](
    legend: Option[String],
    data: F[(A,Double)],
    traverse: Traverse[F]
  ) {
    def legend(legend: String): Categorical[F,A] =
      this.copy(legend = Some(legend))
  }
}
