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
package java2d
package effect

import doodle.core.Color

/** The [[Frame]] specifies how to create a [[Canvas]]. The idiomatic way to
  * create a `Frame` is to start with `Frame.default` and then call the builder
  * methods starting with `with`.
  *
  * For example, this `Frame` specifies a fixed size and a background color.
  *
  * ```
  * Frame.default.withSize(300, 300).withBackground(Color.midnightBlue)
  * ```
  */
final case class Frame(
    size: Size,
    title: String,
    center: Center,
    background: Option[Color],
    redraw: Redraw
) {

  /** Size the canvas with the given fixed dimensions. */
  def withSize(width: Double, height: Double): Frame =
    this.copy(size = Size.fixedSize(width, height))

  /** Size the canvas to fit to the picture's bounding box, plus the given
    * border around the bounding box.
    */
  def withSizedToPicture(border: Int = 20): Frame =
    this.copy(size = Size.fitToPicture(border))

  /** Use the given color as the background.
    */
  def withBackground(color: Color): Frame =
    this.copy(background = Some(color))

  /** Use a fully transparent background.
    */
  def withNoBackground: Frame =
    this.copy(background = None)

  /** When redrawing, clear the screen with the background color. */
  def withClearToBackground: Frame =
    this.copy(redraw = Redraw.clearToBackground)

  /** When redrawing, clear the screen with the given color. */
  def withClearToColor(color: Color): Frame =
    this.copy(redraw = Redraw.clearToColor(color))

  /** Title the window with the given string. */
  def withTitle(title: String): Frame =
    this.copy(title = title)

  /** Make the center of the canvas the center of the picture's bounding box. */
  def withCenterOnPicture: Frame =
    this.copy(center = Center.centeredOnPicture)

  /** Make the center of the canvas the origin. */
  def withCenterAtOrigin: Frame =
    this.copy(center = Center.atOrigin)
}
object Frame {
  val default =
    Frame(
      size = Size.fitToPicture(20),
      title = "Doodle",
      center = Center.centeredOnPicture,
      background = Some(Color.white),
      redraw = Redraw.clearToBackground
    )
}
