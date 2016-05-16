package doodle
package random

import cats.Monad
import scala.util.{Random => Rng}

final case class Random[A](generator: Rng => A) { self =>
  def run(rng: Rng = scala.util.Random): A =
    generator(rng)

  def map[B](f: A => B): Random[B] =
    Random(rng => f(self.run(rng)))

  def flatMap[B](f: A => Random[B]): Random[B] =
    Random(rng => f(self.run(rng)).run(rng))
}
object Random {

  def always[A](in: A): Random[A] =
    Random(rng => in)

  def int: Random[Int] =
    Random(rng => rng.nextInt())

  def natural(upperLimit: Int): Random[Int] =
    Random(rng => rng.nextInt(upperLimit))

  def double: Random[Double] =
    Random(rng => rng.nextDouble())

  def oneOf[A](elts: A*): Random[A] = {
    val length = elts.length
    Random.natural(length) map (idx => elts(idx))
  }

  def discrete[A](elts: (A, Double)*): Random[A] =
    ???

  def normal: Random[Double] =
    Random(rng => rng.nextGaussian())

  def normal(mean: Double, stdDev: Double): Random[Double] =
    normal map (x => (stdDev * x) + mean)

  implicit object randomInstances extends Monad[Random] {
    override def flatMap[A, B](fa: Random[A])(f: (A) ⇒ Random[B]): Random[B] =
      fa.flatMap(f)

    override def map[A, B](fa: Random[A])(f: (A) => B): Random[B] =
      fa.map(f)

    override def pure[A](in: A): Random[A] =
      Random.always(in)
  }
}
