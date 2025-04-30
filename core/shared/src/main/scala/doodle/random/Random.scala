/*
 * Copyright 2015 Creative Scala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package doodle.random

import cats.Comonad
import cats.free.Free

import scala.annotation.tailrec

type Random[A] = Free[RandomOp, A]
extension [A](random: Random[A]) {
  def run(rng: scala.util.Random): A =
    random.run(Random.randomComonad(rng))
}
given defaultRandomComonad: Comonad[RandomOp] =
  Random.randomComonad(scala.util.Random)

sealed abstract class RandomOp[A]
object RandomOp {
  private[random] final case class Always[A](get: A) extends RandomOp[A]
  private[random] final case class Discrete[A](elements: Seq[(A, Double)])
      extends RandomOp[A]
  private[random] final case class Natural(upperLimit: Int)
      extends RandomOp[Int]
  private[random] case object Normal extends RandomOp[Double]
  private[random] case object RInt extends RandomOp[Int]
  private[random] case object RDouble extends RandomOp[Double]
  private[random] final case class Sample[A](sampler: SamplerBuilder[A])
      extends RandomOp[Sampler[A]]
}

object Random {
  import RandomOp.*

  /** Create a `Random` that always generates the given value. */
  def always[A](in: A): Random[A] =
    Free.pure(in)

  /** Create a `Random` that generates an `Int` uniformly distributed across the
    * entire range.
    */
  def int: Random[Int] =
    Free.liftF[RandomOp, Int](RInt)

  /** Create a `Random` that generates an `Int` uniformly distributed in the
    * range greater than or equal to `lower` and less than `upper`.
    */
  def int(lower: Int, upper: Int): Random[Int] = {
    val high = (upper max lower)
    val low = (upper min lower)
    val range = Math.abs(high - low)
    natural(range).map { n =>
      n + low
    }
  }

  /** Create a `Random` that generates an `Int` uniformly distributed in the
    * range greater than or equal to zero and less than `upper`.
    */
  def natural(upperLimit: Int): Random[Int] =
    Free.liftF[RandomOp, Int](Natural(upperLimit))

  /** Create a `Random` that generates a `Double` uniformly distributed between
    * 0.0 and 1.0.
    */
  def double: Random[Double] =
    Free.liftF[RandomOp, Double](RDouble)

  /** Create a `Random` that generates a `Double` uniformly distributed between
    * lower and upper.
    */
  def double(lower: Double, upper: Double): Random[Double] = {
    val high = (upper max lower)
    val low = (upper min lower)
    val range = Math.abs(high - low)
    double.map(d => low + (d * range))
  }

  /** Create a `Random` that generates one of the provided values with uniform
    * distribution.
    */
  def oneOf[A](elts: A*): Random[A] = {
    val length = elts.length
    Random.natural(length).map(idx => elts(idx))
  }

  def discrete[A](elts: (A, Double)*): Random[A] =
    Free.liftF[RandomOp, A](Discrete(elts))

  def normal: Random[Double] =
    Free.liftF[RandomOp, Double](Normal)

  def normal(mean: Double, stdDev: Double): Random[Double] =
    Random.normal.map(x => (stdDev * x) + mean)

  def sampler[A](sampler: SamplerBuilder[A]): Random[Sampler[A]] =
    Free.liftF[RandomOp, Sampler[A]](Sample(sampler))

  def randomComonad(rng: scala.util.Random): Comonad[RandomOp] =
    new Comonad[RandomOp] {
      import RandomOp.*

      @tailrec
      def pick[A](total: Double, weight: Double, events: Seq[(A, Double)]): A =
        events match {
          case (a, p) +: rest =>
            if total < weight && weight < (total + p) then a
            else pick(total + p, weight, rest)
          case _ =>
            throw new Exception("Could not sample---ran out of events!")
        }

      override def coflatMap[A, B](fa: RandomOp[A])(
          f: (RandomOp[A]) => B
      ): RandomOp[B] =
        Always(f(fa))
      override def extract[A](x: RandomOp[A]): A =
        x match {
          case Always(a) => a
          case Discrete(elts) =>
            val weight = rng.nextDouble()
            pick(0.0, weight, elts)
          case Natural(u) => rng.nextInt(u)
          case Normal     => rng.nextGaussian()
          case RInt       => rng.nextInt()
          case RDouble    => rng.nextDouble()
          case Sample(s)  => s(rng)
        }
      override def map[A, B](fa: RandomOp[A])(f: (A) => B): RandomOp[B] =
        Always(f(extract(fa)))
    }
}
