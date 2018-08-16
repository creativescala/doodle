/*
 * Copyright 2015 noelwelsh
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
package algebra

/** Higher level shape primitives */
trait Shape[F[_]] {
  def rectangle(width: Double, height: Double): F[Unit]
  def square(width: Double): F[Unit]
  def triangle(width: Double, height: Double): F[Unit]
  def circle(radius: Double): F[Unit]
  def empty: F[Unit]
}
