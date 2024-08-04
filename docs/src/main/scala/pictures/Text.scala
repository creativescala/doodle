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

package docs
package pictures

import cats.effect.unsafe.implicits.global
import doodle.core.*
import doodle.core.font.Font
import doodle.core.font.FontSize
import doodle.java2d.*
import doodle.syntax.all.*

object Text {
  val hello =
    Picture
      .text("Hello from Doodle!")

  hello.save("pictures/hello.png")

  val font =
    Picture
      .text("Change the font")
      .font(Font.defaultSansSerif.bold.size(FontSize.points(24)))

  font.save("pictures/font.png")
}
