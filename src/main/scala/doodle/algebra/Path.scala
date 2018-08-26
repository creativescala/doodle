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

trait Path[F[_],A] {
  def beginPath(): F[A]
  def closePath(): F[A]

  def stroke(): F[A]
  def fill(): F[A]

  def lineTo(x: Double, y: Double): F[A]
  def moveTo(x: Double, y: Double): F[A]
  def curveTo(cp1X: Double, cp1Y: Double, cp2X: Double, cp2Y: Double, x: Double, y: Double): F[A]
}
