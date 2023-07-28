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
package svg
package effect

import doodle.core.Color

/** The [[Frame]] specifies how to create the area where the SVG output will be
  * drawn. The idiomatic way to create a `Frame` is to start with `Frame(anId)`,
  * where `anId` is the id of the DOM element where the output should be drawn,
  * and then call the builder methods starting with `with`.
  *
  * For example, this `Frame` specifies a fixed size and a background color.
  *
  * ```
  * Frame("svgCanvas").withSize(300, 300).withBackground(Color.midnightBlue)
  * ```
  */
final case class Frame(
    id: String,
    size: Size,
    background: Option[Color] = None
) {

  /** Use the given color as the background.
    */
  def withBackground(color: Color): Frame =
    this.copy(background = Some(color))

  /** Size the canvas to fit to the picture's bounding box, plus the given
    * border around the bounding box.
    */
  def withSizedToPicture(border: Int = 20): Frame =
    this.copy(size = Size.fitToPicture(border))

  /** Size the canvas with the given fixed dimensions. */
  def withSize(width: Double, height: Double): Frame =
    this.copy(size = Size.fixedSize(width, height))
}
object Frame {
  def apply(id: String): Frame =
    Frame(id, Size.fitToPicture(), None)
}
