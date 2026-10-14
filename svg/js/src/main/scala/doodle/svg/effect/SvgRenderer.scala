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
package svg
package effect

import cats.effect.IO
import cats.effect.Resource
import doodle.effect.Renderer

object SvgRenderer extends Renderer[Algebra, Frame, Canvas] {

  import cats.effect.unsafe.implicits.global

  def canvas(description: Frame): Resource[IO, Canvas] =
    Canvas.fromFrame(description)

  def render[A](canvas: Canvas)(picture: Picture[A]): IO[A] =
    canvas.render(picture)

  /** Render a picture to a scalatags `Tag`, leaving the page untouched. The
    * caller decides if, when and where the SVG is added to the document.
    *
    * The frame's `id` is not used, as nothing is drawn to the screen. The rest
    * of the frame still determines the size, background and isolation of the
    * result.
    */
  def renderToTag[A](frame: Frame, picture: Picture[A]): IO[(Tag, A)] =
    Canvas.offscreen(frame).use(canvas => canvas.renderToTag(picture))
}
