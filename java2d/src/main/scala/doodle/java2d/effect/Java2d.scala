/*
 * Copyright 2019 Creative Scala
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
package effect

import doodle.algebra.generic.BoundingBox
import doodle.algebra.generic.reified.Reified
import doodle.core.{Transform=>Tx}
import doodle.java2d.algebra.Graphics2DGraphicsContext
import java.awt.{Graphics2D, RenderingHints}

/** Utilities for rendering with Java2D */
object Java2d {
  def setup(graphics: Graphics2D): Graphics2D = {
    graphics.setRenderingHints(
      new RenderingHints(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON
      )
    )

    graphics
  }

  def render(gc: Graphics2D,
             bb: BoundingBox,
             image: List[Reified],
             width: Double,
             height: Double,
             center: Center): Unit = {
    val tx =
      center match {
        case Center.CenteredOnPicture =>
          // Work out the center of the bounding box, in logical local coordinates
          val centerX = bb.left + (bb.width / 2.0)
          val centerY = bb.bottom + (bb.height / 2.0)
          Tx.translate(-centerX, -centerY)
            .andThen(Tx.logicalToScreen(width, height))

        case Center.AtOrigin =>
          Tx.logicalToScreen(width, height)
      }

    image.foreach { _.render(gc, tx)(Graphics2DGraphicsContext) }
  }

}
