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
package algebra
package reified

import doodle.algebra.generic.Fill
import doodle.algebra.generic.Stroke
import doodle.core.PathElement
import doodle.core.Point
import doodle.core.Transform
import doodle.core.font.Font
import doodle.core.ClosedPath

import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage

/** Render in screen space */
trait GraphicsContext[A] {
  def fillRect(
      gc: A
  )(transform: Transform, fill: Fill, width: Double, height: Double): Unit
  def strokeRect(
      gc: A
  )(transform: Transform, stroke: Stroke, width: Double, height: Double): Unit

  def fillCircle(
      gc: A
  )(transform: Transform, fill: Fill, diameter: Double): Unit
  def strokeCircle(
      gc: A
  )(transform: Transform, stroke: Stroke, diameter: Double): Unit

  def fillPolygon(
      gc: A
  )(transform: Transform, fill: Fill, points: Array[Point]): Unit
  def strokePolygon(
      gc: A
  )(transform: Transform, stroke: Stroke, points: Array[Point]): Unit

  def fillClosedPath(
      gc: A
  )(transform: Transform, fill: Fill, elements: List[PathElement]): Unit
  def strokeClosedPath(
      gc: A
  )(transform: Transform, stroke: Stroke, elements: List[PathElement]): Unit

  def fillOpenPath(
      gc: A
  )(transform: Transform, fill: Fill, elements: List[PathElement]): Unit
  def strokeOpenPath(
      gc: A
  )(transform: Transform, stroke: Stroke, elements: List[PathElement]): Unit

  def bitmap(gc: A)(transform: Transform, image: BufferedImage): Unit

  def text(
      gc: A
  )(
      transform: Transform,
      stroke: Option[Stroke],
      text: String,
      font: Font,
      bounds: Rectangle2D
  ): Unit

  def clip[C](
      gc: A
  )(
      transform: Transform,
      img: Drawing[C],
      clipPath: ClosedPath
  ): Unit
}
