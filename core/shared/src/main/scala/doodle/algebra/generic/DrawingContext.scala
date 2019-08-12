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
package algebra
package generic

import doodle.core.{Cap, Color, Join}

final case class Stroke(color: Color, width: Double, cap: Cap, join: Join, dash: Option[Array[Float]])
final case class Fill(color: Color)

/** Stores state about the current drawing style. */
final case class DrawingContext(
    blendMode: BlendMode,
    strokeColor: Color,
    strokeWidth: Option[Double], // We use strokeWidth to determine if there is a stroke or not
    strokeCap: Cap,
    strokeJoin: Join,
    strokeDash: Option[Array[Float]], // If we don't specify a dash we get the default (which is Array(1.0, 0.0))
    fillColor: Option[Color]
) {
  def blendMode(mode: BlendMode): DrawingContext =
    this.copy(blendMode = mode)

  def stroke: Option[Stroke] =
    (strokeWidth).map(w => Stroke(strokeColor, w, strokeCap, strokeJoin, strokeDash))

  def strokeColor(color: Color): DrawingContext =
    this.copy(strokeColor = color)

  def strokeWidth(width: Double): DrawingContext =
    this.copy(strokeWidth = if (width <= 0) None else Some(width))

  def strokeCap(cap: Cap): DrawingContext =
    this.copy(strokeCap = cap)

  def strokeJoin(join: Join): DrawingContext =
    this.copy(strokeJoin = join)

  def strokeDash(pattern: Array[Float]): DrawingContext =
    this.copy(strokeDash = Some(pattern))

  def noDash: DrawingContext =
    this.copy(strokeDash = None)

  def noStroke: DrawingContext =
    this.copy(strokeWidth = None)

  def fill: Option[Fill] =
    fillColor.map(c => Fill(c))

  def fillColor(color: Color): DrawingContext =
    this.copy(fillColor = Some(color))

  def noFill: DrawingContext =
    this.copy(fillColor = None)
}
object DrawingContext {
  def default: DrawingContext =
    DrawingContext(
      BlendMode.sourceOver,
      Color.black,
      Option(1.0),
      Cap.butt,
      Join.miter,
      None,
      Option.empty
    )
}
