/*
 * Copyright 2015 noelwelsh
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

import cats.syntax.all._
import cats.instances.option._

final case class Stroke[C](color: C, width: Double)
final case class Fill[C](color: C)

/** Stores state about the current drawing style.
  *
  * The type `B` is the type of Blends.
  * The type `C` is the type of Colors.
  */
final case class DrawingContext[B,C](
  blendMode: Option[B],
  strokeWidth: Option[Double],
  strokeColor: Option[C],
  fillColor: Option[C]
) {
  def blendMode(mode: B): DrawingContext[B,C] =
    this.copy(blendMode = blendMode orElse Some(mode))


  def stroke: Option[Stroke[C]] =
    (strokeColor, strokeWidth).mapN((c, w) => Stroke(c, w))

  def strokeColor(color: C): DrawingContext[B,C] =
    this.copy(strokeColor = strokeColor orElse Some(color))

  def strokeWidth(width: Double): DrawingContext[B,C] =
    this.copy(strokeWidth = strokeWidth orElse { if(width <= 0) None else Some(width) })

  def noStroke: DrawingContext[B,C] =
    this.copy(strokeWidth = strokeWidth orElse None)


  def fill: Option[Fill[C]] =
    fillColor.map(c => Fill(c))

  def fillColor(color: C): DrawingContext[B,C] =
    this.copy(fillColor = fillColor orElse Some(color))

  def noFill: DrawingContext[B,C] =
    this.copy(fillColor = fillColor orElse None)
}
object DrawingContext {
  def empty[B,C]: DrawingContext[B,C] = DrawingContext(None,None,None,None)
}
