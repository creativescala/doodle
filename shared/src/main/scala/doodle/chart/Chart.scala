package doodle
package chart

import cats.Traverse
import scala.language.higherKinds

/**
  * The representation of a `Chart`, which can be plotted.
  *
  * A Chart doesn't have interesting recursive structure, but to lower the `DataType` type parameter to the value level we represent `Chart` as an algebraic data type.
  */
sealed abstract class Chart[D <: DataType] extends Product with Serializable {
  def title(title: String): Chart[D]
}
object Chart {
  // Constructors ----------------------------------------------------------

  def scatter[F[_] : Traverse,A : ToPoint](data: F[A]): Chart[DataType.Numerical] =
    Numerical(Series.scatterChart(data),
              xAxis=Axis.numerical,
              yAxis=Axis.numerical,
              title=None)

  // Algebraic data type ---------------------------------------------------

  final case class Numerical[F[_]](
    series: Series.Numerical[F],
    xAxis: Axis.Numerical,
    yAxis: Axis.Numerical,
    title: Option[String]
  ) extends Chart[DataType.Numerical] {
    def title(title: String): Chart[DataType.Numerical] =
      this.copy(title = Some(title))
  }

  final case class Categorical[F[_],A](
    series: Series.Categorical[F,A],
    xAxis: Axis.Categorical,
    yAxis: Axis.Categorical,
    title: Option[String]
  ) extends Chart[DataType.Categorical] {
    def title(title: String): Chart[DataType.Categorical] =
      this.copy(title = Some(title))
  }
}
