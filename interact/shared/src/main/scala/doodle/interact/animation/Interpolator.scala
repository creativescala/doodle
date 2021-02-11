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

import cats.Invariant
import cats.syntax.invariant._
import doodle.core.Angle
import doodle.interact.easing.Easing

/**
  * An interpolator constructs a transducer from a starting value, a stopping value,
  * and the number of elements or steps to produce between these values.
  */
trait Interpolator[A] {

  /**
    * Enumerate a half-open interval, starting with start and ending with stop.
    * The uneased case allows exact computation of the interval while the easing
    * will probably introduce numeric error.
    */
  def halfOpen(start: A, stop: A, steps: Long): Transducer[A]

  /**
    * Enumerate a half-open interval, starting with start and ending with stop,
    * and passed through the given easing.
    */
  def halfOpen(start: A, stop: A, steps: Long, easing: Easing): Transducer[A]

  /**
    * Interpolate a closed interval, starting with start and ending with the
    * first value after stop. The uneased case allows exact computation of the
    * interval while the easing will probably introduce numeric error.
    */
  def closed(start: A, stop: A, steps: Long): Transducer[A]

  /**
    * Interpolate a closed interval, starting with start and ending with the first
    * value after stop, and passed through the given easing.
    */
  def closed(start: A, stop: A, steps: Long, easing: Easing): Transducer[A]
}
object Interpolator {

  /**
    * Invariant functor instance for Interpolator
    */
  implicit object interpolatorInvariant extends Invariant[Interpolator] {
    def imap[A, B](fa: Interpolator[A])(f: A => B)(g: B => A): Interpolator[B] =
      new Interpolator[B] {
        def halfOpen(start: B, stop: B, steps: Long): Transducer[B] =
          fa.halfOpen(g(start), g(stop), steps).map(f)

        def halfOpen(start: B,
                     stop: B,
                     steps: Long,
                     easing: Easing): Transducer[B] =
          fa.halfOpen(g(start), g(stop), steps, easing).map(f)

        def closed(start: B, stop: B, steps: Long): Transducer[B] =
          fa.closed(g(start), g(stop), steps).map(f)

        def closed(start: B,
                   stop: B,
                   steps: Long,
                   easing: Easing): Transducer[B] =
          fa.closed(g(start), g(stop), steps, easing).map(f)
      }
  }

  /**
    * Perform Kahan summation given the total so far, the value to add to the
    * total, and the error term (which starts at 0.0). Returns the updated total
    * and the new error term.
    *
    * Kahan's algorithm is a way to sum floating point numbers that reduces error
    * compared to straightforward addition.
    */
  def kahanSum(total: Double, x: Double, error: Double): (Double, Double) = {
    val y = x - error
    val nextTotal = total + y
    val nextError = (nextTotal - total) - y
    (nextTotal, nextError)
  }

  /**
    * Interpolator instance for Double
    */
  implicit val doubleInterpolator: Interpolator[Double] =
    new Interpolator[Double] {
      def halfOpen(
          start: Double,
          stop: Double,
          steps: Long
      ): Transducer[Double] =
        if (start == stop) Transducer.empty
        else
          new Transducer[Double] {
            // State is the current value and the number of steps
            type State = (Double, Long)

            val increment = (stop - start) / steps

            val initial: State = (start, 0)

            def next(current: State): State = {
              val (x, s) = current
              (x + increment, s + 1)
            }

            def output(state: State): Double = {
              val (x, _) = state
              x
            }

            def stopped(state: State): Boolean = {
              val (x, s) = state
              if (s >= steps) true
              else if (stop >= start) (x >= stop)
              else (x <= stop)
            }
          }

      def halfOpen(
          start: Double,
          stop: Double,
          steps: Long,
          easing: Easing
      ): Transducer[Double] =
        if (start == stop) Transducer.empty
        else
          new Transducer[Double] {
            // The state consists of a number between [0, 1) that we project to
            // [start, stop) and the number of steps taken. We count steps so we
            // can stop exactly at the right time, which otherwise due to numeric
            // error may not happen.
            type State = (Double, Long)

            val increment = 1.0 / steps

            val initial: State = (0.0, 0)

            // Convert [0, 1) to [start, stop)
            def project(x: Double): Double =
              start + (easing(x) * (stop - start))

            def next(current: State): State = {
              val (x, s) = current
              (x + increment, s + 1)
            }

            def output(state: State): Double = {
              val (x, _) = state
              project(x)
            }

            def stopped(state: State): Boolean = {
              val (x, s) = state
              if (s >= steps) true
              else (x >= 1.0)
            }
          }

      def closed(
          start: Double,
          stop: Double,
          steps: Long
      ): Transducer[Double] =
        if (start == stop) Transducer.pure(stop)
        else
          new Transducer[Double] {
            // State = (Current value, Steps, Error)
            // Error is for Kahan summation
            type State = (Double, Long, Double)

            val increment = (stop - start) / (steps - 1)

            val initial: State = (start, 0, 0.0)

            def next(current: State): State = {
              val (total, steps, error) = current
              val (nextTotal, nextError) = kahanSum(total, increment, error)
              (nextTotal, steps + 1, nextError)
            }

            def output(state: State): Double = {
              val (total, s, _) = state
              if (s + 1 >= steps) stop
              else total
            }

            def stopped(state: State): Boolean = {
              val (_, s, _) = state
              (s >= steps)
            }
          }

      def closed(
          start: Double,
          stop: Double,
          steps: Long,
          easing: Easing
      ): Transducer[Double] =
        if (start == stop) Transducer.pure(stop)
        else
          new Transducer[Double] {
            // The state consists of a number between [0, 1] that we project to
            // [start, stop], the number of steps taken, and the error for Kahan
            // summation. We count steps so we can stop exactly at the right
            // time, which otherwise due to numeric error may not happen.
            type State = (Double, Long, Double)

            val increment = 1.0 / (steps - 1)

            val initial: State = (0.0, 0, 0.0)

            // Convert [0, 1] to [start, stop]
            def project(x: Double): Double =
              start + (easing(x) * (stop - start))

            def next(current: State): State = {
              val (total, steps, error) = current
              val (nextTotal, nextError) = kahanSum(total, increment, error)
              (nextTotal, steps + 1, nextError)
            }

            def output(state: State): Double = {
              val (total, s, _) = state
              if (s + 1 >= steps) stop
              else project(total)
            }

            def stopped(state: State): Boolean = {
              val (_, s, _) = state
              (s >= steps)
            }
          }
    }

  /**
    * Interpolator instance for Angle
    */
  implicit val angleInterpolator: Interpolator[Angle] =
    doubleInterpolator.imap(turns => Angle.turns(turns))(angle => angle.toTurns)
}
