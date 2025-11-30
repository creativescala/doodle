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

package doodle.examples

import doodle.algebra.*
import doodle.core.*
import doodle.core.font.*
import doodle.syntax.all.*

/** All the examples from the Text documentation page, written in a backend
  * independent style.
  */
trait TextExamples[Alg <: Layout & Style & Text] extends BaseExamples[Alg] {
  val hello = {
    val txt = text("Hello from Doodle!")

    txt.noStroke
      .fillColor(Color.black)
      .above(txt.noFill)
      .above(txt.fillColor(Color.black))
  }

  val font =
    text("Change the font")
      .strokeColor(Color.blueViolet)
      .fillColor(Color.royalBlue)
      .font(Font.defaultSerif.withBold.withSize(FontSize.points(24)))

  val default =
    text("serif")
      .font(Font.defaultSerif)
      .margin(10)
      .beside(text("sans serif").font(Font.defaultSansSerif).margin(10))
      .beside(text("monospaced").font(Font.defaultMonospaced).margin(10))
      .noStroke
      .fillColor(Color.black)

  val fontFamily =
    text("Helvetica")
      .font(Font(FontFamily.named("Helvetica")))
      .margin(10)
      .beside(
        text("Gill Sans").font(Font(FontFamily.named("Gill Sans"))).margin(10)
      )
      .beside(
        text("Garamond").font(Font(FontFamily.named("Garamond"))).margin(10)
      )
      .beside(
        text("Adwaita Sans")
          .font(Font(FontFamily.named("Adwaita Sans")))
          .margin(10)
      )
      .noStroke
      .fillColor(Color.black)

  // If you add a new example, also add it in here
  val allPictures =
    List(
      hello,
      font,
      default,
      fontFamily
    )
}
