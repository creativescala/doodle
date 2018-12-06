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

trait GenericShape[G] extends Shape[Finalized[G, ?]] {
  implicit val graphicsContext: GraphicsContext[G]

  def rectangle(width: Double, height: Double): Finalized[G, Unit] =
    Finalized.leaf { dc =>
      val strokeWidth = dc.strokeWidth.getOrElse(0.0)
      val bb = BoundingBox.centered(strokeWidth + width, strokeWidth + height)
      (bb, Reified.renderable(dc){ (tx, f) =>
         Reified.fillRect(tx, f, width, height)
       }{ (tx, s) =>
         Reified.strokeRect(tx, s, width, height)
       })
    }

  def square(width: Double): Finalized[G, Unit] =
    rectangle(width, width)

  def triangle(width: Double, height: Double): Finalized[G, Unit] =
    Finalized.leaf { dc =>
      val strokeWidth = dc.strokeWidth.getOrElse(0.0)
      val bb = BoundingBox.centered(strokeWidth + width, strokeWidth + height)
      val w = width / 2.0
      val h = height / 2.0
      val points = Array(Point(-w, -h), Point(0, h), Point(w, -h))

      (bb, Reified.renderable(dc){ (tx, f) =>
         Reified.fillPolygon(tx, f, points)
       }{ (tx, s) =>
          Reified.strokePolygon(tx, s, points)
       })
      // IO {
      //   graphicsContext.fillPolygon(gc)(dc, points)
      //   graphicsContext.strokePolygon(gc)(dc, points)
      // }
    }

  def circle(diameter: Double): Finalized[G, Unit] =
    Finalized.leaf { dc =>
      val strokeWidth = dc.strokeWidth.getOrElse(0.0)
      val bb =
        BoundingBox.centered(strokeWidth + diameter, strokeWidth + diameter)
      (bb, Reified.renderable(dc){ (tx, f) =>
         Reified.fillCircle(tx, f, diameter)
       }{ (tx, s) =>
         Reified.strokeCircle(tx, s, diameter)
       })
      // IO {
      //   graphicsContext.fillCircle(gc)(dc, o, diameter / 2.0)
      //   graphicsContext.strokeCircle(gc)(dc, o, diameter / 2.0)
      // }
    }

  def empty: Finalized[G, Unit] =
    Finalized.leaf { dc =>
      (BoundingBox.empty, Renderable { tx =>
        List.empty[Reified]
      })
    }
}
