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

import cats.data.State
import doodle.core.*
import doodle.core.Transform as Tx

import scala.annotation.tailrec

trait GenericPath[G[_]] extends Path {
  self: Algebra { type Drawing[A] = Finalized[G, A] } =>

  trait PathApi {
    def closedPath(
        tx: Tx,
        fill: Option[Fill],
        stroke: Option[Stroke],
        elements: List[PathElement]
    ): G[Unit]
    def openPath(
        tx: Tx,
        fill: Option[Fill],
        stroke: Option[Stroke],
        elements: List[PathElement]
    ): G[Unit]
  }

  def PathApi: PathApi

  def path(path: ClosedPath): Finalized[G, Unit] =
    Finalized.leaf { dc =>
      val elements = path.elements
      val strokeWidth = dc.strokeWidth.getOrElse(0.0)
      val bb = boundingBox(elements).expand(strokeWidth)

      (
        bb,
        State.inspect(tx =>
          PathApi.closedPath(tx, dc.fill, dc.stroke, elements)
        )
      )
    }

  def path(path: OpenPath): Finalized[G, Unit] =
    Finalized.leaf { dc =>
      val elements = path.elements
      val strokeWidth = dc.strokeWidth.getOrElse(0.0)
      val bb = boundingBox(elements).expand(strokeWidth)

      (
        bb,
        State.inspect(tx => PathApi.openPath(tx, dc.fill, dc.stroke, elements))
      )
    }

  def boundingBox(elements: List[PathElement]): BoundingBox = {
    import PathElement.*

    // This implementation should avoid allocation
    var minX: Double = 0.0
    var minY: Double = 0.0
    var maxX: Double = 0.0
    var maxY: Double = 0.0

    @tailrec
    def iter(elts: List[PathElement]): Unit =
      elts match {
        case hd :: tl =>
          hd match {
            case MoveTo(pos) =>
              minX = pos.x min minX
              minY = pos.y min minY
              maxX = pos.x max maxX
              maxY = pos.y max maxY
            case LineTo(pos) =>
              minX = pos.x min minX
              minY = pos.y min minY
              maxX = pos.x max maxX
              maxY = pos.y max maxY
            case BezierCurveTo(cp1, cp2, pos) =>
              // The control points form a bounding box around a bezier curve,
              // but this may not be a tight bounding box.
              // It's an acceptable solution for now but in the future
              // we may wish to generate a tighter bounding box.
              minX = pos.x min cp2.x min cp1.x min minX
              minY = pos.y min cp2.y min cp1.y min minY
              maxX = pos.x max cp2.x max cp1.x max maxX
              maxY = pos.y max cp2.y max cp1.y max maxY
          }
          iter(tl)
        case Seq() => ()
      }
    iter(elements)

    BoundingBox(minX, maxY, maxX, minY)
  }
}
