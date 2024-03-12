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
package effect

import cats.effect.IO
import doodle.algebra.Algebra
import doodle.algebra.Picture
import doodle.core.format.Format
import doodle.core.{Base64 => B64}

/** The Base64 type represent the ability to encode an image as a Base64 String
  * in a given format.
  */
trait Base64Writer[+Alg <: Algebra, Frame, Fmt <: Format] {
  def base64[A](
      description: Frame,
      picture: Picture[Alg, A]
  ): IO[(A, B64[Fmt])]
  def base64[A](picture: Picture[Alg, A]): IO[(A, B64[Fmt])]
}
