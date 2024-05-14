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
package java2d
package effect

import cats.effect.IO
import doodle.algebra.generic.*
import doodle.core.BoundingBox
import doodle.core.Color
import doodle.core.Transform
import doodle.core.{Transform as Tx}
import doodle.java2d.algebra.Algebra
import doodle.java2d.algebra.Graphics2DGraphicsContext
import doodle.java2d.algebra.reified.Reification
import doodle.java2d.algebra.reified.Reified
import doodle.java2d.algebra.{Java2D as Java2dAlgebra}

import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage

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

  /** Create a transform from local logical coordinates to screen coordinates
    * given the bounding box for a picture, the screen size, and description of
    * the relationship between screen and picture.
    */
  def transform(
      bb: BoundingBox,
      width: Double,
      height: Double,
      center: Center
  ): Tx =
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

  /** Create a transform from screen coordinates to local logical coordinates
    * given the bounding box for a picture, the screen size, and descriptino of
    * the relationship between screen and picture.
    */
  def inverseTransform(
      bb: BoundingBox,
      width: Double,
      height: Double,
      center: Center
  ): Tx =
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

  /** Calculate the size the panel or buffer should be given picture's bounding
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

  def renderBufferedImage[A](
      size: Size,
      center: Center,
      background: Option[Color],
      picture: Picture[A]
  )(makeImage: (Int, Int) => BufferedImage): IO[(BufferedImage, A)] =
    for {
      rendered <- renderGraphics2D(size, center, background, picture) { bb =>
        IO {
          val (w, h) = Java2d.size(bb, size)
          val image = makeImage(w.toInt, h.toInt)

          (Java2d.setup(image.createGraphics()), image)
        }
      }
      (image, a) = rendered
    } yield (image, a)

  private[java2d] def renderGraphics2D[A, I](
      size: Size,
      center: Center,
      background: Option[Color],
      picture: Picture[A]
  )(graphicsContext: BoundingBox => IO[(Graphics2D, I)]): IO[(I, A)] =
    for {
      gc <- IO {
        val bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        Java2d.setup(bi.createGraphics())
      }
      drawing: Finalized[Reification, A] <- IO { picture(Algebra(gc)) }
      (bb, rdr) = drawing.run(List.empty).value
      (_, fa) = rdr.run(Transform.identity).value
      (r, a) = fa.run.value
      (width, height) = Java2d.size(bb, size)
      tx = Java2d.transform(bb, width, height, center)
      contextWithImage <- graphicsContext(bb)
      (gc, image) = contextWithImage
      _ = background.foreach { c =>
        gc.setColor(Java2dAlgebra.toAwtColor(c))
        gc.fillRect(0, 0, width.toInt, height.toInt)
      }
      _ = Java2d.render(gc, r, tx)
    } yield (image, a)
}
