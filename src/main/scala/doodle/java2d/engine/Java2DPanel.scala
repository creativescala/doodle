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
package java2d
package engine

import java.awt.{Dimension, Graphics, Graphics2D}
import cats.effect.IO
import doodle.algebra.generic.{BoundingBox, DrawingContext, Transform}
import doodle.core.Point
import doodle.engine._
import doodle.java2d.algebra.{Algebra,Java2D}
import java.awt.{Dimension,Graphics,Graphics2D}
import java.util.NoSuchElementException
import javax.swing.{JPanel, SwingUtilities}
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.SyncVar

final class Java2DPanel(frame: Frame) extends JPanel {
  import doodle.engine.Size._
  import Java2DPanel.RenderRequest

  private val images: ArrayBuffer[Contextualized[_]] = ArrayBuffer.empty
  private val channel: SyncVar[RenderRequest[_]] = new SyncVar()

  frame.size match {
    case FixedSize(w, h) =>
      setPreferredSize(new Dimension(w.toInt, h.toInt))
      SwingUtilities.windowForComponent(this).pack()

    case _ => ()
  }

  def maybeResize(bb: BoundingBox): Unit = {
    frame.size match {
      case FitToImage(border) =>
        setPreferredSize(new Dimension(bb.width.toInt + border, bb.height.toInt + border))
        SwingUtilities.windowForComponent(this).pack()

      case FixedSize(_, _) =>
        // Should have set this at start up
        ()

      case FullScreen => ???
    }
  }

  def render[A](f: Algebra => Drawing[A]): IO[A] = {
    def register(cb: Either[Throwable, A] => Unit): Unit = {
      val drawing   = f(Algebra())
      val (bb, ctx) = drawing(DrawingContext.default)

      val rr = RenderRequest(bb, ctx, cb)
      channel.put(rr)
      this.repaint()
    }

    IO.async(register)
  }

  override def paintComponent(context: Graphics): Unit = {
    val gc = context.asInstanceOf[Graphics2D]
    Java2D.setup(gc)

    try {
      val rr = channel.take(10L)
      val bb = rr.boundingBox
      maybeResize(bb)

      images += rr.context
      rr.render(gc, getWidth, getHeight).unsafeRunSync()
    } catch {
      case _: NoSuchElementException => ()
        val tx = Transform.logicalToScreen(getWidth.toDouble, getHeight.toDouble)
        images.foreach(context => context((gc, tx))(Point.zero).unsafeRunSync())
        ()
    }
  }
}
object Java2DPanel {
  final case class RenderRequest[A](boundingBox: BoundingBox, context: Contextualized[A], cb: Either[Throwable, A] => Unit) {
    def render(gc: Graphics2D, width: Int, height: Int): IO[Unit] = {
      val tx = Transform.logicalToScreen(width.toDouble, height.toDouble)
      for {
        a <- context((gc, tx))(Point.zero)
      } yield cb(Right(a))
    }
  }
}
