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
package reified

import cats.implicits._
import doodle.core.{PathElement, Point, Transform => Tx}
import doodle.algebra.generic.{Fill, Stroke}

sealed abstract class Reified extends Product with Serializable {
  def transform: Tx
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

  // def renderable[F[_]: InvariantMonoidal](dc: DrawingContext)(fill: (Tx, Fill) => Reified)(
  //     stroke: (Tx, Stroke) => Reified): Renderable[F,Unit] =
  //   Renderable { tx =>
  //     val f = dc.fill.map { f =>
  //       List(fill(tx, f))
  //     }
  //     val s = dc.stroke.map { s =>
  //       List(stroke(tx, s))
  //     }

  //     (f |+| s).getOrElse(List.empty[Reified])
  //   }

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
