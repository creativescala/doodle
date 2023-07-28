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
import cats.effect.unsafe.IORuntime
import doodle.core.BoundingBox
import doodle.core.Normalized
import doodle.core.Transform
import doodle.java2d.algebra.Algebra
import doodle.java2d.algebra.Java2D
import doodle.java2d.algebra.reified.Reified

import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import javax.swing.JPanel
import javax.swing.SwingUtilities
import scala.collection.mutable.ArrayBuffer

final class Java2DPanel(frame: Frame)(implicit runtime: IORuntime)
    extends JPanel {
  import Java2DPanel.RenderRequest

  /** The channel communicates between the Swing thread and outside threads
    */
  private val channel: LinkedBlockingQueue[RenderRequest[?]] =
    new LinkedBlockingQueue(1)

  /** The pictures we've rendered, along with the bounding box for each picture.
    * Ordered so the last element is the most recent picture (which should be
    * rendered last).
    *
    * Default size is 1 as the most common case is being asked to render only
    * one picture.
    *
    * As an optimization with check the [[Redraw]] property of the [[Frame]],
    * and if we use an opaque color to redraw we only keep the last element
    * around. See [[opaqueRedraw]].
    */
  private val pictures: ArrayBuffer[(BoundingBox, List[Reified])] =
    new ArrayBuffer(1)

  /** True if the redraw is an opaque color and hence we don't need to keep
    * earlier pictures around.
    */
  private val opaqueRedraw =
    frame.redraw match {
      case Redraw.ClearToBackground =>
        frame.background match {
          case None    => true
          case Some(c) => c.alpha == Normalized.MaxValue
        }
      case Redraw.ClearToColor(c) =>
        c.alpha == Normalized.MaxValue
    }

  def resize(width: Double, height: Double): Unit = {
    setPreferredSize(new Dimension(width.toInt, height.toInt))
    SwingUtilities.windowForComponent(this).pack()
  }

  def render[A](request: RenderRequest[A]): Unit = {
    channel.put(request)
    // println("Java2DPanel put in the channel")
    this.repaint()
    // println("Java2DPanel repaint request sent")
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
        var i = 0
        while (i < pictures.size) {
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

          val (bb, reified) = pictures(i)
          val tx = Java2d.transform(
            bb,
            getWidth.toDouble,
            getHeight.toDouble,
            frame.center
          )

          Java2d.render(gc, reified, tx)

          i = i + 1
        }
    }
  }

  override def paintComponent(context: Graphics): Unit = {
    // println("Java2DPanel painting")
    val gc = context.asInstanceOf[Graphics2D]
    Java2d.setup(gc)

    val algebra = Algebra(gc)

    val rr = channel.poll(10L, TimeUnit.MILLISECONDS)
    if (rr == null) ()
    else {
      val result = rr.render(algebra).unsafeRunSync()
      val bb = result.boundingBox
      val picture = result.reified
      resize(result.width, result.height)
      if (opaqueRedraw && pictures.size > 0)
        pictures.update(0, (bb, picture))
      else
        pictures += ((bb, picture))

    }

    draw(gc)
  }
}
object Java2DPanel {
  final case class RenderResult[A](
      boundingBox: BoundingBox,
      width: Double,
      height: Double,
      reified: List[Reified],
      value: A
  )

  final case class RenderRequest[A](
      picture: Picture[A],
      frame: Frame,
      cb: Either[Throwable, RenderResult[A]] => Unit
  ) {
    def render(algebra: Algebra): IO[RenderResult[A]] = {
      IO {
        val drawing = picture(algebra)
        val (bb, rdr) = drawing.runA(List.empty).value
        val (w, h) = Java2d.size(bb, frame.size)
        val (_, fa) = rdr.run(Transform.identity).value
        val (reified, a) = fa.run.value
        val result = RenderResult(bb, w, h, reified, a)
        cb(Right(result))

        result
      }
    }
  }
}
