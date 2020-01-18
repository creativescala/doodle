/*
 * Copyright 2015-2020 Noel Welsh
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

package doodle
package interact
package animation

import cats.{Functor, Semigroupal}
import doodle.interact.easing.Easing
import scala.concurrent.duration.Duration

/**
  * An Interpolation represents a range of values between a starting and ending
  * value. The interpolation is also optionally transformed by an easing
  * function. The starting value is mapped to 0.0 and the ending value to 1.0.
  * When a number of steps is specified the interpolation is transformed into a
  * transducer, which may be run or composed with other transducers.
  *
  * Interpolations may be transformed by map and product (functor and
  * semigroupal) to construct more complex interpolations.
  *
  * The differences between an interpolation and a transducer are as follows:
  *
  * - An interpolation specifies a start and end value. When a number of steps is
  *   given it becomes a transducer. When interpolations are combined in
  *   parallel, with product, they always take the same amount of time when
  *   converted to a transducer. Transducers combined in parallel may take
  *   differing amounts of time.
  *
  * - A transducer can represent arbitrary FSMs, while an interpolation moves
  *   from start to end value.
  *
  * - Transducers can be run. Interpolations must be transformed to transducers
  *   to run.
  */
sealed trait Interpolation[A] {
  import Interpolation._

  /**
    * Transform the output of this interpolation with the given function.
    */
  def map[B](f: A => B): Interpolation[B] =
    Map(this, f)

  /**
    * Combine this Interpolation in parallel with that Interpolation.
    */
  def product[B](that: Interpolation[B]): Interpolation[(A, B)] =
    Product(this, that)

  /**
    * Apply an easing function to this interpolation.
    *
    * Map the range in this interpolation to 0.0 and 1.0, pass through the given
    * easing function, and then map back to the original domain.
    */
  def withEasing(easing: Easing): Interpolation[A] =
    WithEasing(this, easing)

  /**
    * Create a transducer that will produce the given number of values before it
    * stops. So, for example, calling `forSteps(2)` will create a transducer that
    * produces 2 values before it stops.
    *
    * The number of steps must be non-negative. 0 steps means a transducer that
    * stops immediately. 1 step will produce the start value for a half-open
    * interval and the stop value for a closed interval.
    */
  def forSteps(steps: Long): Transducer[A] = {
    // This method serves to make type inference happier by introducing the new type variable C.
    def loop[C](
        interpolation: Interpolation[C],
        easing: Option[Easing]
    ): Transducer[C] =
      interpolation match {
        case WithEasing(source, e) =>
          // Use outermost easing
          if (easing.isEmpty) loop(source, Some(e))
          else loop(source, easing)
        case Map(source, f) => loop(source, easing).map(f)
        case Product(l, r)  => loop(l, easing).product(loop(r, easing))
        case HalfOpen(start, stop, i) =>
          easing match {
            case Some(e) => i.halfOpen(start, stop, steps, e)
            case None    => i.halfOpen(start, stop, steps)
          }
        case Closed(start, stop, i) =>
          easing match {
            case Some(e) => i.closed(start, stop, steps, e)
            case None    => i.closed(start, stop, steps)
          }
      }

    loop(this, None)
  }

  def forDuration(duration: Duration): Transducer[A] =
    forSteps(duration.toMillis * 60 / 1000)
}
object Interpolation {
  final case class HalfOpen[A](
      start: A,
      stop: A,
      interpolator: Interpolator[A]
  ) extends Interpolation[A]
  final case class Closed[A](
      start: A,
      stop: A,
      interpolator: Interpolator[A]
  ) extends Interpolation[A]
  final case class WithEasing[A](source: Interpolation[A], easing: Easing)
      extends Interpolation[A]
  // Essentially a free applicative
  final case class Map[A, B](source: Interpolation[A], f: A => B)
      extends Interpolation[B]
  final case class Product[A, B](
      left: Interpolation[A],
      right: Interpolation[B]
  ) extends Interpolation[(A, B)]

  implicit val interpolationInstance
      : Functor[Interpolation] with Semigroupal[Interpolation] =
    new Functor[Interpolation] with Semigroupal[Interpolation] {
      def product[A, B](
          fa: Interpolation[A],
          fb: Interpolation[B]
      ): Interpolation[(A, B)] =
        fa.product(fb)

      def map[A, B](fa: Interpolation[A])(f: A => B): Interpolation[B] =
        fa.map(f)
    }

  /**
    * Construct a half-open interpolation, which starts at the given start value
    * and ends at (but does not generate) the given stop value.
    */
  def halfOpen[A](start: A, stop: A)(
      implicit i: Interpolator[A]
  ): Interpolation[A] =
    HalfOpen(start, stop, i)

  /**
    * Construct a closed interpolation, which starts at the given start value
    * and ends at the given stop value.
    */
  def closed[A](start: A, stop: A)(
      implicit i: Interpolator[A]
  ): Interpolation[A] =
    Closed(start, stop, i)

}
