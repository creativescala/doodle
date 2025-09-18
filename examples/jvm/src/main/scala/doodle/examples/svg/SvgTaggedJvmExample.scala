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

package doodle.examples.svg

import cats.effect.unsafe.implicits.global
import doodle.core.Color
import doodle.effect.DefaultFrame
import doodle.svg.*
import doodle.svg.effect.Frame
import doodle.svg.syntax.all.*
import doodle.syntax.all.*

object SvgTaggedJvmExample {

  implicit val frame: doodle.effect.DefaultFrame[Frame] =
    new DefaultFrame[Frame] {
      override def default: Frame = doodle.svg.effect.Frame("frame1")
    }

  def drawLink(id: String): Unit =
    Picture
      .regularPolygon(7, 15)
      .strokeWidth(5.0)
      .beside(Picture.text("Creative Scala").noStroke.fillColor(Color.black))
      .link("https://creativescala.org/")
      .write("link_tagged.svg")
}
