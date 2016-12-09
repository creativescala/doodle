package doodle
package chart

import doodle.core.Point

/**
  * Type class for converting `A` to a `Point`
  */
trait ToPoint[A] {
  def toPoint(in: A): Point
}
object ToPoint {
  import scala.math.Numeric
  implicit def numericTupleToPoint[A,B](implicit numA: Numeric[A], numB: Numeric[B]): ToPoint[(A,B)] =
    new ToPoint[(A,B)] {
      def toPoint(in: (A,B)): Point = {
        val (a,b) = in
        Point(numA.toDouble(a), numB.toDouble(b))
      }
    }

  implicit val identityToPoint: ToPoint[Point] =
    new ToPoint[Point] {
      def toPoint(in: Point): Point = in
    }
}
