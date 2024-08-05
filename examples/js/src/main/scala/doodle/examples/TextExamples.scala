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
  val hello =
    text("Hello from Doodle!")
      .fillColor(Color.black)

  val font =
    text("Change the font")
      .strokeColor(Color.blueViolet)
      .fillColor(Color.royalBlue)
      .font(Font.defaultSerif.bold.size(FontSize.points(24)))
  //
  // If you add a new example, also add it in here
  val allPictures =
    List(
      hello,
      font
    )
}
