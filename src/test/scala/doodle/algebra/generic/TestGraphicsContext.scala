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

object TestGraphicsContext {
  def log(): Log = new Log()

  class Log {
    private var messages: List[Reify.GraphicsContext] = List.empty

    def add(message: Reify.GraphicsContext): Unit =
      messages = message :: messages

    def log: List[Reify.GraphicsContext] = messages.reverse
  }

  implicit val logGraphicsContext = new GraphicsContext[Log] {
    def fillRect(gc: Log)(dc: DrawingContext,
                          center: Point,
                          width: Double,
                          height: Double): Unit =
      gc.add(Reify.GraphicsContext.fillRect(dc, center, width, height))
    def strokeRect(gc: Log)(dc: DrawingContext,
                            center: Point,
                            width: Double,
                            height: Double): Unit =
      gc.add(Reify.GraphicsContext.strokeRect(dc, center, width, height))

    def fillCircle(
        gc: Log)(dc: DrawingContext, center: Point, radius: Double): Unit =
      gc.add(Reify.GraphicsContext.fillCircle(dc, center, radius))
    def strokeCircle(
        gc: Log)(dc: DrawingContext, center: Point, radius: Double): Unit =
      gc.add(Reify.GraphicsContext.strokeCircle(dc, center, radius))

    def fillPolygon(gc: Log)(dc: DrawingContext, points: Array[Point]): Unit =
      gc.add(Reify.GraphicsContext.fillPolygon(dc, points))
    def strokePolygon(gc: Log)(dc: DrawingContext, points: Array[Point]): Unit =
      gc.add(Reify.GraphicsContext.strokePolygon(dc, points))

    def fillClosedPath(gc: Log)(dc: DrawingContext,
                                center: Point,
                                elements: List[PathElement]): Unit =
      gc.add(Reify.GraphicsContext.fillClosedPath(dc, center, elements))
    def strokeClosedPath(gc: Log)(dc: DrawingContext,
                                  center: Point,
                                  elements: List[PathElement]): Unit =
      gc.add(Reify.GraphicsContext.strokeClosedPath(dc, center, elements))

    def fillOpenPath(gc: Log)(dc: DrawingContext,
                              center: Point,
                              elements: List[PathElement]): Unit =
      gc.add(Reify.GraphicsContext.fillOpenPath(dc, center, elements))
    def strokeOpenPath(gc: Log)(dc: DrawingContext,
                                center: Point,
                                elements: List[PathElement]): Unit =
      gc.add(Reify.GraphicsContext.strokeOpenPath(dc, center, elements))
  }
}
