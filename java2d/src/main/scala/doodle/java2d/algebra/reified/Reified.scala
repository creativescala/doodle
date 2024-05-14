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
import doodle.core.font.Font
import doodle.core.{Transform as Tx}

import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage

/** Each element of `Reified` is an instruction to draw something on the screen
  * or to otherwise alter the state the graphics context.
  *
  * Each instruction should be atomic: there should be *no* nesting of
  * instructions inside instructions. In compiler terms, this is a "linear IR",
  * not a "tree IR".
  *
  * When defining a `Picture`, there are many operations that apply to some
  * group of elements. For example, rotating a `Picture` or setting a stroke
  * color applies to all the elements within the `Picture` on which the method
  * is called. This has a natural representation as a tree, but we need to use a
  * different strategy to represent it as a linear list of instructions. There
  * are two implementation approaches:
  *
  *   - Push all these operations into the atomic instructions. This is the
  *     approach currently taken, with each element containing the transform,
  *     and fill or stroke as appropriate. The advantage of this approach is
  *     that each reified instruction is independent of any other. The
  *     disadvantage is that this doesn't scale as the amount of context grows,
  *     as each instruction needs to have additional fields added.
  *
  *   - Have stateful operations to add and remove some context. For example,
  *     push and pop a transform or stroke color. This is the approach taken in
  *     the `Graphics2D` Java API. For example, calling the `transform` method
  *     on Graphics2D adds the transform to the already existing transforms.
  *     (However, the API lacks methods to undo these operations, which makes it
  *     a bit limited.)
  */
sealed abstract class Reified extends Product with Serializable {
  import Reified.*

  def transform: Tx

  /** finalTransform gives an transform applied after any other reified
    * transform. Usually this is a transform from logical to screen coordinates.
    */
  def render[A](gc: A, finalTransform: Tx)(implicit
      ctx: GraphicsContext[A]
  ): Unit =
    this match {
      case FillOpenPath(tx, fill, elements) =>
        ctx.fillOpenPath(gc)(tx.andThen(finalTransform), fill, elements)
      case StrokeOpenPath(tx, stroke, elements) =>
        ctx.strokeOpenPath(gc)(tx.andThen(finalTransform), stroke, elements)

      case FillClosedPath(tx, fill, elements) =>
        ctx.fillClosedPath(gc)(tx.andThen(finalTransform), fill, elements)
      case StrokeClosedPath(tx, stroke, elements) =>
        ctx.strokeClosedPath(gc)(tx.andThen(finalTransform), stroke, elements)

      case FillCircle(tx, fill, diameter) =>
        ctx.fillCircle(gc)(tx.andThen(finalTransform), fill, diameter)
      case StrokeCircle(tx, stroke, diameter) =>
        ctx.strokeCircle(gc)(tx.andThen(finalTransform), stroke, diameter)

      case FillRect(tx, fill, width, height) =>
        ctx.fillRect(gc)(tx.andThen(finalTransform), fill, width, height)
      case StrokeRect(tx, stroke, width, height) =>
        ctx.strokeRect(gc)(tx.andThen(finalTransform), stroke, width, height)

      case FillPolygon(tx, fill, points) =>
        ctx.fillPolygon(gc)(tx.andThen(finalTransform), fill, points)
      case StrokePolygon(tx, stroke, points) =>
        ctx.strokePolygon(gc)(tx.andThen(finalTransform), stroke, points)

      case Bitmap(tx, image) =>
        ctx.bitmap(gc)(tx.andThen(finalTransform), image)

      case Text(tx, _, stroke, text, font, bounds) =>
        ctx.text(gc)(tx.andThen(finalTransform), stroke, text, font, bounds)
    }
}
object Reified {
  import PathElement.*

  def transform(tx: Tx, elements: List[PathElement]): List[PathElement] =
    elements.map {
      case MoveTo(to) => MoveTo(tx(to))
      case LineTo(to) => LineTo(tx(to))
      case BezierCurveTo(cp1, cp2, to) =>
        BezierCurveTo(tx(cp1), tx(cp2), tx(to))
    }
  def transform(tx: Tx, points: Array[Point]): Array[Point] =
    points.map { pt =>
      tx(pt)
    }

  final case class FillRect(
      transform: Tx,
      fill: Fill,
      width: Double,
      height: Double
  ) extends Reified
  final case class StrokeRect(
      transform: Tx,
      stroke: Stroke,
      width: Double,
      height: Double
  ) extends Reified

  final case class FillCircle(transform: Tx, fill: Fill, diameter: Double)
      extends Reified
  final case class StrokeCircle(transform: Tx, stroke: Stroke, diameter: Double)
      extends Reified

  final case class FillPolygon(transform: Tx, fill: Fill, points: Array[Point])
      extends Reified
  final case class StrokePolygon(
      transform: Tx,
      stroke: Stroke,
      points: Array[Point]
  ) extends Reified

  final case class FillClosedPath(
      transform: Tx,
      fill: Fill,
      elements: List[PathElement]
  ) extends Reified
  final case class StrokeClosedPath(
      transform: Tx,
      stroke: Stroke,
      elements: List[PathElement]
  ) extends Reified

  final case class FillOpenPath(
      transform: Tx,
      fill: Fill,
      elements: List[PathElement]
  ) extends Reified
  final case class StrokeOpenPath(
      transform: Tx,
      stroke: Stroke,
      elements: List[PathElement]
  ) extends Reified
  final case class Bitmap(transform: Tx, image: BufferedImage) extends Reified
  final case class Text(
      transform: Tx,
      fill: Option[Fill],
      stroke: Option[Stroke],
      text: String,
      font: Font,
      bounds: Rectangle2D
  ) extends Reified

  def fillRect(
      transform: Tx,
      fill: Fill,
      width: Double,
      height: Double
  ): Reified =
    FillRect(transform, fill, width, height)
  def strokeRect(
      transform: Tx,
      stroke: Stroke,
      width: Double,
      height: Double
  ): Reified =
    StrokeRect(transform, stroke, width, height)

  def fillCircle(transform: Tx, fill: Fill, diameter: Double): Reified =
    FillCircle(transform, fill, diameter)
  def strokeCircle(transform: Tx, stroke: Stroke, diameter: Double): Reified =
    StrokeCircle(transform, stroke, diameter)

  def fillPolygon(transform: Tx, fill: Fill, points: Array[Point]): Reified =
    FillPolygon(transform, fill, points)
  def strokePolygon(
      transform: Tx,
      stroke: Stroke,
      points: Array[Point]
  ): Reified =
    StrokePolygon(transform, stroke, points)

  def fillClosedPath(
      transform: Tx,
      fill: Fill,
      elements: List[PathElement]
  ): Reified =
    FillClosedPath(transform, fill, elements)
  def strokeClosedPath(
      transform: Tx,
      stroke: Stroke,
      elements: List[PathElement]
  ): Reified =
    StrokeClosedPath(transform, stroke, elements)

  def fillOpenPath(
      transform: Tx,
      fill: Fill,
      elements: List[PathElement]
  ): Reified =
    FillOpenPath(transform, fill, elements)
  def strokeOpenPath(
      transform: Tx,
      stroke: Stroke,
      elements: List[PathElement]
  ): Reified =
    StrokeOpenPath(transform, stroke, elements)
  def bitmap(transform: Tx, image: BufferedImage): Reified =
    Bitmap(transform, image)

  def text(
      transform: Tx,
      fill: Option[Fill],
      stroke: Option[Stroke],
      text: String,
      font: Font,
      bounds: Rectangle2D
  ): Reified =
    Text(transform, fill, stroke, text, font, bounds)
}
