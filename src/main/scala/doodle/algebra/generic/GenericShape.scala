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

import cats.effect.IO
import doodle.core._

trait GenericShape[G] extends Shape[Finalized[G,?],Unit] {
  implicit val graphicsContext: GraphicsContext[G]

  def rectangle(width: Double, height: Double): Finalized[G,Unit] =
    Finalized{ dc =>
      val strokeWidth = dc.strokeWidth.getOrElse(0.0)
      val bb = BoundingBox.centered(strokeWidth + width, strokeWidth + height)
      (bb,
       Contextualized{ (gc, tx) =>
         Renderable{ origin =>
           val o = tx(origin)
           IO {
             graphicsContext.fillRect(gc)(dc, o, width, height)
             graphicsContext.strokeRect(gc)(dc, o, width, height)
           }
         }
       })
    }

  def square(width: Double): Finalized[G,Unit] =
    rectangle(width, width)

  def triangle(width: Double, height: Double): Finalized[G,Unit] =
    Finalized{ dc =>
      val strokeWidth = dc.strokeWidth.getOrElse(0.0)
      val bb = BoundingBox.centered(strokeWidth + width, strokeWidth + height)
      (bb,
       Contextualized{ (gc, tx) =>
         Renderable { origin =>
           val w = width / 2.0
           val h = height / 2.0
           val points = Array(tx(origin + Vec(-w, -h)),
                              tx(origin + Vec(0, h)),
                              tx(origin + Vec(w, -h)))
           IO {
             graphicsContext.fillPolygon(gc)(dc, points)
             graphicsContext.strokePolygon(gc)(dc, points)
           }
         }
       })
    }

  def circle(radius: Double): Finalized[G,Unit] =
    Finalized{ dc =>
      val strokeWidth = dc.strokeWidth.getOrElse(0.0)
      val diameter = radius * 2.0
      val bb = BoundingBox.centered(strokeWidth + diameter, strokeWidth + diameter)
      (bb,
       Contextualized{ (gc, tx) =>
         Renderable { origin =>
           val o = tx(origin)
           IO {
             graphicsContext.fillCircle(gc)(dc, o, radius)
             graphicsContext.strokeCircle(gc)(dc, o, radius)
           }
         }
       })
    }

  def empty: Finalized[G,Unit] =
    Finalized{ dc =>
      (BoundingBox.empty,
       Contextualized{ (gc, tx) =>
         Renderable{ origin => IO{()} }
       })
    }
}
