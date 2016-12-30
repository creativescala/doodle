package doodle
package chart

/*
import cats.Traverse
import scala.language.higherKinds

/**
  * Type class for converting `A` (typically a list of points)---something that can be turned into a data series in a plot.
  */
trait ToCategorical[F[_],A] {
  def toCategorial(in: F[A]): Categorical[F,A]
}
object ToCategorical {
  implicit def traverseTuplesToSeries[F[_],A](implicit t: Traverse[F]): ToCategorical[F,(A, Double)] =
    new ToCategorical[F,(A, Double)] {
      def toCategorial(in: F[(A, Double)]): Categorical[F,A] = {
        Categorical(in, t)
      }
    }
}
trait ToRatio[F[_],A] {
  def toRatio(in: F[A]): Ratio[F]
}
object ToRatio {
  implicit def traverseToPointTuplesToSeries[F[_],A](implicit t: Traverse[F], p: ToPoint[A]): ToRatio[F,A] =
    new ToRatio[F,A] {
      import doodle.chart.syntax._

      def toRatio(in: F[A]): Ratio[F] = {
        Ratio(t.map(in)(_.toPoint), t)
      }
    }
}
 */
