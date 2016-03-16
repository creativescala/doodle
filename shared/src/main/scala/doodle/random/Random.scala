package doodle
package random

import cats.Monad
import scala.util.{Random => Rng}

final case class Random[A](generator: Rng => A) { self =>
  def run(rng: Rng): A =
    generator(rng)

  def map[B](f: A => B): Random[B] =
    Random(rng => f(self.run(rng)))

  def flatMap[B](f: A => Random[B]): Random[B] =
    Random(rng => f(self.run(rng)).run(rng))
}
object Random {

  def int: Random[Int] =
    Random(rng => rng.nextInt())

  def positiveIntLessThan(upperLimit: Int): Random[Int] =
    Random(rng => rng.nextInt(upperLimit))

  def double: Random[Double] =
    Random(rng => rng.nextDouble())

  def gaussian: Random[Double] =
    Random(rng => rng.nextGaussian())

  def gaussian(mean: Double, stdDev: Double): Random[Double] =
    gaussian map (x => (stdDev * x) + mean)


  implicit object randomInstances extends Monad[Random] {
    override def flatMap[A, B](fa: Random[A])(f: (A) ⇒ Random[B]): Random[B] =
      fa.flatMap(f)

    override def map[A, B](fa: Random[A])(f: (A) => B): Random[B] =
      fa.map(f)

    override def pure[A](x: A): Random[A] =
      Random(rng => x)
  }
}
