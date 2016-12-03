package doodle
package chart

import doodle.core.Point

/**
  * Type class for converting `A` to a `Point`
  */
trait AsPoint[A] {
  def asPoint(in: A): Point
}
object AsPoint {
  import scala.math.Numeric
  implicit def numericTupleAsPoint[A,B](implicit numA: Numeric[A], numB: Numeric[B]): AsPoint[(A,B)] =
    new AsPoint[(A,B)] {
      def asPoint(in: (A,B)): Point = {
        val (a,b) = in
        Point(numA.toDouble(a), numB.toDouble(b))
      }
    }

  implicit val identityAsPoint: AsPoint[Point] =
    new AsPoint[Point] {
      def asPoint(in: Point): Point = in
    }
}
