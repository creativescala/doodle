package doodle
package random

import cats.free.Free

object Random {
  import RandomOp._

  def always[A](in: A): Random[A] =
    Free.pure(in)

  def int: Random[Int] =
    Free.liftF[RandomOp,Int](RInt)

  def natural(upperLimit: Int): Random[Int] =
    Free.liftF[RandomOp,Int](Natural(upperLimit))

  def double: Random[Double] =
    Free.liftF[RandomOp,Double](RDouble)

  def oneOf[A](elts: A*): Random[A] = {
    val length = elts.length
    Random.natural(length) map (idx => elts(idx))
  }

  def discrete[A](elts: (A, Double)*): Random[A] =
    ???

  def normal: Random[Double] =
    Free.liftF[RandomOp,Double](Gaussian)

  def normal(mean: Double, stdDev: Double): Random[Double] =
    normal map (x => (stdDev * x) + mean)
}
