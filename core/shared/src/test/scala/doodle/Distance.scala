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

package doodle

trait Distance[A] {
  def distance(a1: A, a2: A): Double
}
object Distance {
  import doodle.core._

  def apply[A](f: (A, A) => Double): Distance[A] =
    new Distance[A] {
      def distance(a1: A, a2: A): Double =
        f(a1, a2)
    }

  implicit val angleDistance: Distance[Angle] =
    Distance((a1, a2) => Math.abs((a1 - a2).toRadians))

  implicit val pointDistance: Distance[Point] =
    Distance((pt1, pt2) => (pt1 - pt2).length)

  implicit val doubleDistance: Distance[Double] =
    Distance((v1, v2) => Math.abs(v1 - v2))
}
