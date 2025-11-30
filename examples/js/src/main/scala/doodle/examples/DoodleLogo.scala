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
import doodle.core.*
import doodle.core.font.*
import doodle.svg.*
import doodle.syntax.all.*

import scala.scalajs.js.annotation.*

@JSExportTopLevel("DoodleLogo")
object DoodleLogo {
  val font =
    Font.defaultSansSerif
      .withSize(FontSize.points(22))
      .withWeight(FontWeight.bold)
  val logo: Picture[Unit] =
    (0.to(10))
      .map(i =>
        Picture
          .text("Doodle SVG")
          .font(font)
          .fillColor(Color.hotPink.spin(10.degrees * i.toDouble))
          .at(i * 3.0, i * -3.0)
      )
      .toList
      .allOn

  @JSExport
  def draw(mount: String) =
    logo.drawWithFrame(Frame(mount))
}
