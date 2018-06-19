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
package generic

import doodle.core._

object Reify {
  sealed abstract class GraphicsContext extends Product with Serializable {
    def dc: DrawingContext
  }
  object GraphicsContext {
    final case class FillRect(dc: DrawingContext,
                              center: Point,
                              width: Double,
                              height: Double)
        extends GraphicsContext
    final case class StrokeRect(dc: DrawingContext,
                                center: Point,
                                width: Double,
                                height: Double)
        extends GraphicsContext
    final case class FillCircle(dc: DrawingContext,
                                center: Point,
                                radius: Double)
        extends GraphicsContext
    final case class StrokeCircle(dc: DrawingContext,
                                  center: Point,
                                  radius: Double)
        extends GraphicsContext
    final case class FillPolygon(dc: DrawingContext, points: Array[Point])
        extends GraphicsContext
    final case class StrokePolygon(dc: DrawingContext, points: Array[Point])
        extends GraphicsContext
    final case class FillClosedPath(dc: DrawingContext,
                                    center: Point,
                                    elements: List[PathElement])
        extends GraphicsContext
    final case class StrokeClosedPath(dc: DrawingContext,
                                      center: Point,
                                      elements: List[PathElement])
        extends GraphicsContext
    final case class FillOpenPath(dc: DrawingContext,
                                  center: Point,
                                  elements: List[PathElement])
        extends GraphicsContext
    final case class StrokeOpenPath(dc: DrawingContext,
                                    center: Point,
                                    elements: List[PathElement])
        extends GraphicsContext

    def fillRect(dc: DrawingContext,
                 center: Point,
                 width: Double,
                 height: Double): GraphicsContext =
      FillRect(dc, center, width, height)
    def strokeRect(dc: DrawingContext,
                   center: Point,
                   width: Double,
                   height: Double): GraphicsContext =
      StrokeRect(dc, center, width, height)

    def fillCircle(dc: DrawingContext,
                   center: Point,
                   radius: Double): GraphicsContext =
      FillCircle(dc, center, radius)
    def strokeCircle(dc: DrawingContext,
                     center: Point,
                     radius: Double): GraphicsContext =
      StrokeCircle(dc, center, radius)

    def fillPolygon(dc: DrawingContext, points: Array[Point]): GraphicsContext =
      FillPolygon(dc, points)
    def strokePolygon(dc: DrawingContext,
                      points: Array[Point]): GraphicsContext =
      StrokePolygon(dc, points)

    def fillClosedPath(dc: DrawingContext,
                       center: Point,
                       elements: List[PathElement]): GraphicsContext =
      FillClosedPath(dc, center, elements)
    def strokeClosedPath(dc: DrawingContext,
                         center: Point,
                         elements: List[PathElement]): GraphicsContext =
      StrokeClosedPath(dc, center, elements)

    def fillOpenPath(dc: DrawingContext,
                     center: Point,
                     elements: List[PathElement]): GraphicsContext =
      FillOpenPath(dc, center, elements)
    def strokeOpenPath(dc: DrawingContext,
                       center: Point,
                       elements: List[PathElement]): GraphicsContext =
      StrokeOpenPath(dc, center, elements)
  }
}
