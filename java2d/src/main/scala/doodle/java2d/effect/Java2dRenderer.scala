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
package java2d
package effect

import cats.effect.IO
import cats.effect.Resource
import doodle.effect.Renderer

object Java2dRenderer extends Renderer[Algebra, Frame, Canvas] {
  def canvas(description: Frame): Resource[IO, Canvas] =
    Canvas(description)

  def render[A](canvas: Canvas)(picture: Picture[A]): IO[A] =
    canvas.render(picture)
}
