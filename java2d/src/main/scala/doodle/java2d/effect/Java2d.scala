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
package java2d
package effect

import doodle.algebra.generic.BoundingBox
import doodle.core.{Transform => Tx}
import doodle.java2d.algebra.Graphics2DGraphicsContext
import doodle.java2d.algebra.reified.Reified
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

  /**
   * Create a transform from local logical coordinates to screen coordinates
   * given the bounding box for a picture, the screen size, and description of
   * the relationship between screen and picture.
   */
  def transform(bb: BoundingBox,
                width: Double,
                height: Double,
                center: Center): Tx =
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

  /**
   * Create a transform from screen coordinates to local logical coordinates
   * given the bounding box for a picture, the screen size, and descriptino of
   * the relationship between screen and picture.
   */
  def inverseTransform(bb: BoundingBox,
                width: Double,
                height: Double,
                center: Center): Tx =
    center match {
      case Center.CenteredOnPicture =>
        // Work out the center of the bounding box, in logical local coordinates
        val centerX = bb.left + (bb.width / 2.0)
        val centerY = bb.bottom + (bb.height / 2.0)
        Tx.screenToLogical(width, height)
          .andThen(Tx.translate(centerX, centerY))

      case Center.AtOrigin =>
        Tx.screenToLogical(width, height)
    }

  /**
   * Calculate the size the panel or buffer should be given picture's bounding
   * box and the frame description.
   */
  def size(bb: BoundingBox, size: Size): (Double, Double) = {
    size match {
      case Size.FitToImage(border) =>
        (bb.width + border, bb.height + border)

      case Size.FixedSize(w, h) =>
        (w, h)
    }
  }

  def render(gc: Graphics2D, image: List[Reified], transform: Tx): Unit = {
    image.foreach { _.render(gc, transform)(Graphics2DGraphicsContext) }
  }

}
