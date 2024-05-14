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
package golden

import cats.implicits.*
import doodle.core.*
import doodle.core.font.*
import doodle.java2d.*
import doodle.syntax.all.*
import munit.*

class Text extends FunSuite with GoldenPicture {
  // Enusre the text images have the same size, which guards to some extent
  // against different font rendering on different machines
  val spacer = square[Algebra](100).noFill.noStroke

  testPicture("text-serif-default") {
    text[Algebra]("Hi!").font(Font.defaultSerif).on(spacer)
  }

  testPicture("text-sans-serif-default") {
    text[Algebra]("Hi!").font(Font.defaultSansSerif).on(spacer)
  }

  testPicture("text-serif-48pt") {
    text[Algebra]("Hi!").font(Font.defaultSerif.size(48)).on(spacer)
  }

  testPicture("text-sans-serif-48pt") {
    text[Algebra]("Hi!")
      .font(Font.defaultSansSerif.size(48))
      .on(spacer)
  }

  // Test that layout is correct. Text should be centered on the circle
  testPicture("text-on-circle") {
    text[Algebra]("Hi!")
      .on(circle[Algebra](40))
  }

  testPicture("text-color") {
    text[Algebra]("Red")
      .strokeColor(Color.red)
      .beside(text[Algebra]("Blue").strokeColor(Color.blue))
      .font(Font.defaultSerif.size(24).family("Arial"))
      .on(rectangle(115, 50).fillColor(Color.white).noStroke)
  }
}
