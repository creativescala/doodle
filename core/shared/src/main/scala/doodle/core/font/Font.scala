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

sealed abstract class FontFace extends Product with Serializable
object FontFace {
  final case object Bold extends FontFace
  final case object Italic extends FontFace
  final case object Normal extends FontFace

  val bold: FontFace = Bold
  val italic: FontFace = Italic
  val normal: FontFace = Normal
}

sealed abstract class FontSize extends Product with Serializable
object FontSize {
  final case class Points(get: Int) extends FontSize

  def points(pts: Int): FontSize = Points(pts)
}

final case class Font(family: FontFamily, face: FontFace, size: FontSize)
object Font {
  import FontFamily._
  import FontFace._
  import FontSize._

  val defaultSerif = Font(serif, normal, points(12))
  val defaultSansSerif = Font(sansSerif, normal, points(12))
}
