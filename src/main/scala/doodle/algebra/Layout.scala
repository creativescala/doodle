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

trait Layout[F[_],A] {
  def on(top: F[A], bottom: F[A]): F[A]
  def beside(left: F[A], right: F[A]): F[A]
  def above(top: F[A], bottom: F[A]): F[A]

  def under(bottom: F[A], top: F[A]): F[A] =
    on(top, bottom)
  def below(bottom: F[A], top: F[A]): F[A] =
    above(top, bottom)
}
