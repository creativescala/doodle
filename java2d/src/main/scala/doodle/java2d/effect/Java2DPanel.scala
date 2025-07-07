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

import doodle.core.BoundingBox
import doodle.core.Normalized
import doodle.core.Point
import doodle.core.Transform
import doodle.java2d.algebra.Algebra
import doodle.java2d.algebra.Java2D
import doodle.java2d.algebra.reified.Reified
import doodle.java2d.effect.Size.FitToImage
import doodle.java2d.effect.Size.FixedSize

import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.*
import java.util.concurrent.CompletableFuture
import javax.swing.JPanel
import javax.swing.SwingUtilities
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/** A Swing component (a JPanel) that can render a Doodle Picture.
  *
  * The majority of the code within here runs on the Swing thread. To avoid
  * deadlock, communication with the outside world must be mediated through
  * concurrency safe data structures or similar means.
  */
final class Java2DPanel(
    frame: Frame,
    mouseClickQueue: BlockingCircularQueue[Point],
    mouseMoveQueue: BlockingCircularQueue[Point]
) extends JPanel {

  /** The pictures we've been requested to render, but have not yet done so.
    * Access to this should only be done via the Swing thread.
    */
  private val requests: mutable.Queue[RenderRequest[?]] = mutable.Queue()

  /** The pictures we've rendered, along with the bounding box for each picture.
    * Ordered so the last element is the most recent picture (which should be
    * rendered last).
    *
    * Default size is 1 as the most common case is being asked to render only
    * one picture.
    *
    * As an optimization we check the [[Redraw]] property of the [[Frame]], and
    * if we use an opaque color to redraw we only keep the last element around.
    * See [[opaqueRedraw]].
    */
  private val pictures: ArrayBuffer[(BoundingBox, List[Reified])] =
    new ArrayBuffer(1)

  /** Converts from the screen coordinates to Doodle's coordinates. Must only be
    * accessed from the Swing thread to avoid race conditions.
    */
  private var inverseTx: Transform = Transform.identity

  /** True if the redraw is an opaque color and hence we don't need to keep
    * earlier pictures around.
    */
  private val opaqueRedraw: Boolean =
    frame.redraw match {
      case Redraw.ClearToBackground =>
        frame.background match {
          case None    => true
          case Some(c) => c.alpha == Normalized.MaxValue
        }
      case Redraw.ClearToColor(c) =>
        c.alpha == Normalized.MaxValue
    }

  this.addMouseListener(
    new MouseListener {

      def mouseClicked(e: MouseEvent): Unit = {
        val pt = e.getPoint()
        mouseClickQueue
          .add(inverseTx(Point(pt.getX(), pt.getY())))
        ()
      }

      def mouseEntered(e: MouseEvent): Unit = ()
      def mouseExited(e: MouseEvent): Unit = ()
      def mousePressed(e: MouseEvent): Unit = ()
      def mouseReleased(e: MouseEvent): Unit = ()
    }
  )

  this.addMouseMotionListener(
    new MouseMotionListener {

      def mouseDragged(e: MouseEvent): Unit = ()
      def mouseMoved(e: MouseEvent): Unit = {
        val pt = e.getPoint()
        mouseMoveQueue
          .add(inverseTx(Point(pt.getX(), pt.getY())))
        ()
      }
    }
  )

  // A fixed size frame allows us to set the panel size and inverse transform
  // without a picture present
  frame.size match {
    case FitToImage(border)       => ()
    case FixedSize(width, height) =>
      setSize(width.toInt, height.toInt)
      // resize(width, height)
      inverseTx = Java2d.inverseTransform(
        BoundingBox.centered(width, height),
        width,
        height,
        frame.center
      )
  }

  def resize(width: Double, height: Double): Unit = {
    setPreferredSize(Dimension(width.toInt, height.toInt))
    SwingUtilities.windowForComponent(this).pack()
  }

  /** Queue a picture to be drawn. Returns a CompletableFuture that will be
    * completed when the pictures has been drawn.
    */
  def render[A](picture: Picture[A]): CompletableFuture[A] = {
    val f: CompletableFuture[A] = CompletableFuture()

    // This runs on the Swing thread, and so it's safe to access requests
    SwingUtilities.invokeLater { () =>
      val request = RenderRequest(picture, f)
      requests.enqueue(request)
      this.repaint()
    }

    f
  }

  /** Draw all images this [[Java2DPanel]] has received. We assume the
    * Graphics2D parameter has already been setup.
    */
  def draw(gc: Graphics2D): Unit = {
    // Clear to background
    frame.background.foreach { c =>
      gc.setColor(Java2D.toAwtColor(c))
      gc.fillRect(0, 0, getWidth(), getHeight())
    }

    pictures.size match {
      case 0 =>
        // Nothing to do
        ()

      case 1 =>
        val (bb, reified) = pictures(0)
        val tx = Java2d.transform(
          bb,
          getWidth.toDouble,
          getHeight.toDouble,
          frame.center
        )

        Java2d.render(gc, reified, tx)

      case _ =>
        val (bb, reified) = pictures(0)
        val tx = Java2d.transform(
          bb,
          getWidth.toDouble,
          getHeight.toDouble,
          frame.center
        )

        Java2d.render(gc, reified, tx)

        // Draw remaining images, redrawing *before* each image
        pictures.foreach { case (bb, reified) =>
          frame.redraw match {
            case Redraw.ClearToBackground =>
              frame.background.foreach { c =>
                gc.setColor(Java2D.toAwtColor(c))
                gc.fillRect(0, 0, getWidth(), getHeight())
              }

            case Redraw.ClearToColor(c) =>
              gc.setColor(Java2D.toAwtColor(c))
              gc.fillRect(0, 0, getWidth(), getHeight())
          }

          val tx = Java2d.transform(
            bb,
            getWidth.toDouble,
            getHeight.toDouble,
            frame.center
          )

          Java2d.render(gc, reified, tx)
        }

    }
  }

  override def paintComponent(context: Graphics): Unit = {
    super.paintComponents(context)
    val gc = context.asInstanceOf[Graphics2D]
    Java2d.setup(gc)

    val algebra = Algebra(gc)

    while requests.size > 0 do {
      val r = requests.dequeue
      val result = r.render(frame, algebra)

      val bb = result.boundingBox
      val reified = result.reified

      resize(result.width, result.height)
      if opaqueRedraw && pictures.size > 0 then
        pictures.update(0, (bb, reified))
      else pictures += ((bb, reified))

      inverseTx = Java2d.inverseTransform(
        result.boundingBox,
        result.width,
        result.height,
        frame.center
      )
    }

    draw(gc)
  }

}
