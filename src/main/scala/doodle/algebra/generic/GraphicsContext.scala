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

import doodle.core.Point

/** Render in screen space */
trait GraphicsContext[A] {
  def fillRect(gc: A)(dc: DrawingContext, center: Point, width: Double, height: Double): Unit
  def strokeRect(gc: A)(dc: DrawingContext, center: Point, width: Double, height: Double): Unit

  def fillCircle(gc: A)(dc: DrawingContext, center: Point, radius: Double): Unit
  def strokeCircle(gc: A)(dc: DrawingContext, center: Point, radius: Double): Unit

  def fillPolygon(gc: A)(dc: DrawingContext, points: Array[Point]): Unit
  def strokePolygon(gc: A)(dc: DrawingContext, points: Array[Point]): Unit
}
