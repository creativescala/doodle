package doodle

import cats.Comonad
import cats.free.Free
import scala.util.{Random => Rng}
import scala.annotation.tailrec

object random {
  type Random[A] = Free[RandomOp,A]

  sealed abstract class RandomOp[A]
  object RandomOp {
    final case class Always[A](get: A) extends RandomOp[A]
    final case class Discrete[A](elements: Seq[(A, Double)]) extends RandomOp[A]
    final case object RInt extends RandomOp[Int]
    final case class Natural(upperLimit: Int) extends RandomOp[Int]
    final case object RDouble extends RandomOp[Double]
    final case object Normal extends RandomOp[Double]
  }

  implicit def randomInstances(implicit rng: Rng = scala.util.Random): Comonad[RandomOp] =
    new Comonad[RandomOp] {
      import RandomOp._

      @tailrec
      def pick[A](total: Double, weight: Double, events: Seq[(A, Double)]): A =
        events match {
          case (a, p) :: rest =>
            if(total < weight && weight < (total + p))
              a
            else
              pick(total + p, weight, rest)
          case Nil =>
            throw new Exception("Could not sample---ran out of events!")
        }

      override def coflatMap[A, B](fa: RandomOp[A])(f: (RandomOp[A]) ⇒ B): RandomOp[B] =
        Always(f(fa))
      override def extract[A](x: RandomOp[A]): A =
        x match {
          case Always(a) => a
          case Discrete(elts) =>
            val weight = rng.nextDouble()
            pick(0.0, weight, elts)
          case RInt => rng.nextInt()
          case Natural(u) => rng.nextInt(u)
          case RDouble => rng.nextDouble()
          case Normal => rng.nextGaussian()
        }
      override def map[A, B](fa: RandomOp[A])(f: (A) ⇒ B): RandomOp[B] =
        Always(f(extract(fa)))
    }

  object Random {
    import RandomOp._

    /** Create a `Random` that always generates the given value. */
    def always[A](in: A): Random[A] =
      Free.pure(in)

    /** Create a `Random` that generates an `Int` uniformly distributed across the entire range. */
    def int: Random[Int] =
      Free.liftF[RandomOp,Int](RInt)

    /** Create a `Random` that generates an `Int` uniformly distributed in the range greater than or equal to `lower` and less than `upper`. */
    def int(lower: Int, upper: Int): Random[Int] = {
      val high = (upper max lower)
      val low = (upper min lower)
      val range = Math.abs(high - low)
      natural(range).map { n => n + low }
    }

    /** Create a `Random` that generates an `Int` uniformly distributed in the range greater than or equal to zero and less than `upper`. */
    def natural(upperLimit: Int): Random[Int] =
      Free.liftF[RandomOp,Int](Natural(upperLimit))

    /** Create a `Random` that generates a `Double` uniformly distributed between 0.0 and 1.0. */
    def double: Random[Double] =
      Free.liftF[RandomOp,Double](RDouble)

    /** Create a `Random` that generates one of the provided values with uniform distribution. */
    def oneOf[A](elts: A*): Random[A] = {
      val length = elts.length
      Random.natural(length).map (idx => elts(idx))
    }

    def discrete[A](elts: (A, Double)*): Random[A] =
      Free.liftF[RandomOp,A](Discrete(elts))

    def normal: Random[Double] =
      Free.liftF[RandomOp,Double](Normal)

    def normal(mean: Double, stdDev: Double): Random[Double] =
      Random.normal.map (x => (stdDev * x) + mean)
  }

}
