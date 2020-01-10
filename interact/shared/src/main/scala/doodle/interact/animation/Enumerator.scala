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

/**
  * An enumerator constructs a tranducer from a starting value, a stopping value,
  * and the number of elements or steps to produce between these values.
  */
trait Enumerator[A] {

  /**
    * Enumerate a half-open interval, starting with start and ending with stop.
    */
  def upto(start: A, stop: A, steps: Long): Transducer[A]

  /**
    * Enumerate a closed interval, starting with start and ending with the first
    * value after stop.
    */
  def uptoIncluding(start: A, stop: A, steps: Long): Transducer[A]
}
object Enumerator {
  implicit val doubleEnumerator: Enumerator[Double] =
    new Enumerator[Double] {
      def upto(start: Double, stop: Double, steps: Long): Transducer[Double] =
        new Transducer[Double] {
          // The state consists of the current number we output and the number
          // of steps taken. Due to numeric error (start + steps * increment) may not
          // exactly equal stop. Hence we stop whenever we've taken step steps.
          type State = (Double, Long)

          val increment = (stop - start) / steps

          val initial: State = (start, 0)

          def next(current: State): State = {
            val (c, s) = current
            (c + increment, s + 1)
          }

          def output(state: State): Double = {
            val (c, _) = state
            c
          }

          def stopped(state: State): Boolean = {
            val (c, s) = state
            if (s >= steps) true
            else if (stop >= start) c >= stop
            else c <= stop
          }
        }

      def uptoIncluding(
          start: Double,
          stop: Double,
          steps: Long
      ): Transducer[Double] =
        if (start == stop) Transducer.pure(stop)
        else
          new Transducer[Double] {
            // The state consists of the current number we output and the number
            // of steps taken. Due to numeric error (start + steps * increment) may not
            // exactly equal stop. Hence we stop whenever we've taken step steps.
            type State = (Double, Long)

            val increment = (stop - start) / steps

            val initial: State = (start, 0)

            def next(current: State): State = {
              val (c, s) = current
              (c + increment, s + 1)
            }

            def output(state: State): Double = {
              val (c, s) = state
              if (s+1 == steps) stop
              else c
            }

            def stopped(state: State): Boolean = {
              val (c, s) = state
              if (s >= steps) true
              else if (stop >= start) c > stop
              else c < stop
            }
          }
    }
}
