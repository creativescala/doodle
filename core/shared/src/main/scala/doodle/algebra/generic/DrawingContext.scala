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
package algebra
package generic

import doodle.core.{Cap, Color, Gradient, Join}
import doodle.core.font.Font

final case class Stroke(color: Color, width: Double, cap: Cap, join: Join, dash: Option[Array[Float]])
sealed trait Fill extends Product with Serializable
object Fill {
  final case class ColorFill(color: Color) extends Fill
  final case class GradientFill(gradient: Gradient) extends Fill

  def color(color: Color): Fill =
    ColorFill(color)

  def gradient(gradient: Gradient): Fill =
    GradientFill(gradient)
}

/** Stores state about the current drawing style. */
final case class DrawingContext(
    blendMode: BlendMode,
    strokeColor: Color,
    strokeWidth: Option[Double], // We use strokeWidth to determine if there is a stroke or not
    strokeCap: Cap,
    strokeJoin: Join,
    strokeDash: Option[Array[Float]], // If we don't specify a dash we get the default (which is Array(1.0, 0.0))
    fill: Option[Fill],
    font: Font
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

  def fillColor(color: Color): DrawingContext =
    this.copy(fill = Some(Fill.color(color)))

  def fillGradient(gradient: Gradient): DrawingContext =
    this.copy(fill = Some(Fill.gradient(gradient)))

  def noFill: DrawingContext =
    this.copy(fill = None)

  def font(font: Font): DrawingContext =
    this.copy(font = font)
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
      Option.empty,
      Font.defaultSansSerif
    )
}
