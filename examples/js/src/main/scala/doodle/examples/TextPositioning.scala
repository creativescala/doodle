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

import cats.effect.unsafe.implicits.global
import doodle.core._
import doodle.core.font._
import doodle.svg._
import doodle.syntax.all._

import scala.scalajs.js.annotation._

@JSExportTopLevel("TextPositioning")
object TextPositioning {
  val font = Font.defaultSansSerif.size(FontSize.points(18))
  def text(string: String): Picture[Unit] =
    Picture.text(string).font(font).fillColor(Color.hotpink)

  val textPositioning: Picture[Unit] =
    text("Above")
      .above(
        text("Center").on(Picture.circle(100).strokeWidth(3.0))
      )
      .above(text("Below"))

  @JSExport
  def draw(mount: String) =
    textPositioning.drawWithFrame(Frame(mount))
}
