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
package java2d
package effect

import doodle.core.Color

/** The [[Frame]] specifies how to create a [[Canvas]].
  */
final case class Frame(
    size: Size,
    title: String = "Doodle",
    center: Center,
    background: Option[Color] = Some(Color.white),
    redraw: Redraw = Redraw.clearToBackground
) {

  /** Change the background color to the given color.
    */
  def background(color: Color): Frame =
    this.copy(background = Some(color))

  /** Change the background to fully transparent (no background).
    */
  def noBackground: Frame =
    this.copy(background = None)

  def clearToBackground: Frame =
    this.copy(redraw = Redraw.clearToBackground)

  def clearToColor(color: Color): Frame =
    this.copy(redraw = Redraw.clearToColor(color))

  def title(title: String): Frame =
    this.copy(title = title)

  def centerOnPicture: Frame =
    this.copy(center = Center.centeredOnPicture)

  def centerAtOrigin: Frame =
    this.copy(center = Center.atOrigin)
}
object Frame {
  def fitToPicture(border: Int = 20): Frame =
    Frame(Size.fitToPicture(border), center = Center.centeredOnPicture)

  def size(width: Double, height: Double): Frame =
    Frame(Size.fixedSize(width, height), center = Center.atOrigin)
}
