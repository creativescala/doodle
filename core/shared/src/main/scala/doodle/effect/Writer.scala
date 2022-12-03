/*
 * Copyright 2015 Noel Welsh
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
package effect

import cats.effect.IO
import doodle.algebra.Algebra
import doodle.algebra.Picture
import doodle.core.format.Format

import java.io.File

/** The `Writer` typeclass represents write a picture to a file in a given
  * format.
  */
trait Writer[+Alg <: Algebra, Frame, Fmt <: Format] {
  def write[A](file: File, description: Frame, image: Picture[Alg, A]): IO[A]
  def write[A](file: File, image: Picture[Alg, A]): IO[A]
}
