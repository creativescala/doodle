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
import cats.effect.Resource
import doodle.algebra.Algebra
import doodle.algebra.Picture

/** The `Renderer` typeclass describes a data type that can create an area to
  * render a picture (a Canvas) from a description (a Frame) and render a
  * picture to that Canvas.
  */
trait Renderer[+Alg <: Algebra, Frame, Canvas] {

  /** Construct a Canvas from a description. */
  def canvas(description: Frame): Resource[IO, Canvas]

  /** Render a picture to a Canvas. */
  def render[A](canvas: Canvas)(picture: Picture[Alg, A]): IO[A]
}
object Renderer {
  def apply[Alg <: Algebra, Frame, Canvas](implicit
      renderer: Renderer[Alg, Frame, Canvas]
  ): Renderer[Alg, Frame, Canvas] = renderer
}
