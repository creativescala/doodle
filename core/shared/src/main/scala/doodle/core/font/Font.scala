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
package core
package font

enum FontFamily {
  case Serif
  case SansSerif
  case Monospaced
  case Named(get: String)
}
object FontFamily {
  import FontFamily.*

  val serif: FontFamily = Serif
  val sansSerif: FontFamily = SansSerif
  val monospaced: FontFamily = Monospaced
  def named(name: String): FontFamily = Named(name)
}

enum FontWeight {
  case Normal
  case Bold
}
object FontWeight {
  import FontWeight.*

  val bold: FontWeight = Bold
  val normal: FontWeight = Normal
}

enum FontStyle {
  case Italic
  case Normal

}
object FontStyle {
  import FontStyle.*

  val italic: FontStyle = Italic
  val normal: FontStyle = Normal
}

enum FontSize {
  case Points(get: Int)
}
object FontSize {
  import FontSize.*

  def points(pts: Int): FontSize = Points(pts)
}

final case class Font(
    family: FontFamily,
    style: FontStyle = FontStyle.normal,
    weight: FontWeight = FontWeight.normal,
    size: FontSize = FontSize.points(12)
) {
  def withFamily(name: String): Font =
    withFamily(FontFamily.named(name))

  def withFamily(family: FontFamily): Font =
    this.copy(family = family)

  def withStyle(style: FontStyle) =
    this.copy(style = style)

  def withItalic: Font =
    withStyle(FontStyle.italic)

  def withBold: Font =
    withWeight(FontWeight.bold)

  def withWeight(weight: FontWeight): Font =
    this.copy(weight = weight)

  def withSize(points: Int): Font =
    withSize(FontSize.points(points))

  def withSize(size: FontSize): Font =
    this.copy(size = size)

}
object Font {
  import FontFamily.*
  import FontSize.*

  val defaultSerif =
    Font(serif, FontStyle.normal, FontWeight.normal, points(12))
  val defaultSansSerif =
    Font(sansSerif, FontStyle.normal, FontWeight.normal, points(12))
  val defaultMonospaced =
    Font(monospaced, FontStyle.normal, FontWeight.normal, points(12))
}
