package doodle

import cats.{Comonad,Monad}
import cats.free.Free
import scala.util.{Random => Rng}

package object random {
  type Random[A] = Free[RandomOp,A]

  sealed abstract class RandomOp[A]
  object RandomOp {
    final case class Always[A](get: A) extends RandomOp[A]
    final case object RInt extends RandomOp[Int]
    final case class Natural(upperLimit: Int) extends RandomOp[Int]
    final case object RDouble extends RandomOp[Double]
    final case object Gaussian extends RandomOp[Double]
  }

  implicit val randomInstance: Monad[Random] = Free.freeMonad

  implicit def randomInstances(implicit rng: Rng = scala.util.Random): Comonad[RandomOp] =
    new Comonad[RandomOp] {
      import RandomOp._

      override def coflatMap[A, B](fa: RandomOp[A])(f: (RandomOp[A]) ⇒ B): RandomOp[B] =
        Always(f(fa))
      override def extract[A](x: RandomOp[A]): A =
        x match {
          case Always(a) => a
          case RInt => rng.nextInt()
          case Natural(u) => rng.nextInt(u)
          case RDouble => rng.nextDouble()
          case Gaussian => rng.nextGaussian()
        }
      override def map[A, B](fa: RandomOp[A])(f: (A) ⇒ B): RandomOp[B] =
        Always(f(extract(fa)))
    }
}
