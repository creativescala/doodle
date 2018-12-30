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

import cats.Eval
import cats.implicits._
import doodle.core.{PathElement, Point, Transform => Tx}

sealed abstract class Reified extends Product with Serializable {
  import Reified._

  def transform: Tx

  /** finalTransform gives an transform applied after any other reified transform.
    * Usually this is a transfrom from logical to screen coordinates. */
  def render[A](gc: A, finalTransform: Tx)(
      implicit ctx: GraphicsContext[A]): Unit =
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

    }
}
object Reified {
  import PathElement._

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

  def renderable(dc: DrawingContext)(fill: (Tx, Fill) => Reified)(
      stroke: (Tx, Stroke) => Reified): Renderable[Unit] =
    Renderable { tx =>
      val f = dc.fill.map { f =>
        List(fill(tx, f))
      }
      val s = dc.stroke.map { s =>
        List(stroke(tx, s))
      }

      Eval.now(((f |+| s).getOrElse(List.empty[Reified]), ()))
    }

  final case class FillRect(transform: Tx,
                            fill: Fill,
                            width: Double,
                            height: Double)
      extends Reified
  final case class StrokeRect(transform: Tx,
                              stroke: Stroke,
                              width: Double,
                              height: Double)
      extends Reified

  final case class FillCircle(transform: Tx, fill: Fill, diameter: Double)
      extends Reified
  final case class StrokeCircle(transform: Tx, stroke: Stroke, diameter: Double)
      extends Reified

  final case class FillPolygon(transform: Tx, fill: Fill, points: Array[Point])
      extends Reified
  final case class StrokePolygon(transform: Tx,
                                 stroke: Stroke,
                                 points: Array[Point])
      extends Reified

  final case class FillClosedPath(transform: Tx,
                                  fill: Fill,
                                  elements: List[PathElement])
      extends Reified
  final case class StrokeClosedPath(transform: Tx,
                                    stroke: Stroke,
                                    elements: List[PathElement])
      extends Reified

  final case class FillOpenPath(transform: Tx,
                                fill: Fill,
                                elements: List[PathElement])
      extends Reified
  final case class StrokeOpenPath(transform: Tx,
                                  stroke: Stroke,
                                  elements: List[PathElement])
      extends Reified

  def fillRect(transform: Tx,
               fill: Fill,
               width: Double,
               height: Double): Reified =
    FillRect(transform, fill, width, height)
  def strokeRect(transform: Tx,
                 stroke: Stroke,
                 width: Double,
                 height: Double): Reified =
    StrokeRect(transform, stroke, width, height)

  def fillCircle(transform: Tx, fill: Fill, diameter: Double): Reified =
    FillCircle(transform, fill, diameter)
  def strokeCircle(transform: Tx, stroke: Stroke, diameter: Double): Reified =
    StrokeCircle(transform, stroke, diameter)

  def fillPolygon(transform: Tx, fill: Fill, points: Array[Point]): Reified =
    FillPolygon(transform, fill, points)
  def strokePolygon(transform: Tx,
                    stroke: Stroke,
                    points: Array[Point]): Reified =
    StrokePolygon(transform, stroke, points)

  def fillClosedPath(transform: Tx,
                     fill: Fill,
                     elements: List[PathElement]): Reified =
    FillClosedPath(transform, fill, elements)
  def strokeClosedPath(transform: Tx,
                       stroke: Stroke,
                       elements: List[PathElement]): Reified =
    StrokeClosedPath(transform, stroke, elements)

  def fillOpenPath(transform: Tx,
                   fill: Fill,
                   elements: List[PathElement]): Reified =
    FillOpenPath(transform, fill, elements)
  def strokeOpenPath(transform: Tx,
                     stroke: Stroke,
                     elements: List[PathElement]): Reified =
    StrokeOpenPath(transform, stroke, elements)
}
