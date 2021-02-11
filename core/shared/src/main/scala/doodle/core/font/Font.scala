/*
 * Copyright 2015-2020 Noel Welsh
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

sealed abstract class FontFamily extends Product with Serializable
object FontFamily {
  final case object Serif extends FontFamily
  final case object SansSerif extends FontFamily
  final case object Monospaced extends FontFamily
  final case class Named(get: String) extends FontFamily

  val serif: FontFamily = Serif
  val sansSerif: FontFamily = SansSerif
  val monospaced: FontFamily = Monospaced
  def named(name: String): FontFamily = Named(name)
}

sealed abstract class FontWeight extends Product with Serializable
object FontWeight {
  final case object Normal extends FontWeight
  final case object Bold extends FontWeight

  val bold: FontWeight = Bold
  val normal: FontWeight = Normal
}

sealed abstract class FontStyle extends Product with Serializable
object FontStyle {
  final case object Italic extends FontStyle
  final case object Normal extends FontStyle

  val italic: FontStyle = Italic
  val normal: FontStyle = Normal
}

sealed abstract class FontSize extends Product with Serializable
object FontSize {
  final case class Points(get: Int) extends FontSize

  def points(pts: Int): FontSize = Points(pts)
}

final case class Font(family: FontFamily,
                      style: FontStyle,
                      weight: FontWeight,
                      size: FontSize) {
  def family(name: String): Font =
    family(FontFamily.named(name))

  def family(family: FontFamily): Font =
    this.copy(family = family)

  def italic: Font =
    style(FontStyle.italic)

  def style(style: FontStyle) =
    this.copy(style = style)

  def bold: Font =
    weight(FontWeight.bold)

  def weight(weight: FontWeight): Font =
    this.copy(weight = weight)

  def size(points: Int): Font =
    size(FontSize.points(points))

  def size(size: FontSize): Font =
    this.copy(size = size)

}
object Font {
  import FontFamily._
  import FontSize._

  val defaultSerif =
    Font(serif, FontStyle.normal, FontWeight.normal, points(12))
  val defaultSansSerif =
    Font(sansSerif, FontStyle.normal, FontWeight.normal, points(12))
}
