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

import doodle.algebra.generic.{DrawingContext,Transform}
import doodle.core.Point
import doodle.engine._
import doodle.java2d.algebra.{Algebra,Java2D}
import java.awt.{Dimension,Graphics,Graphics2D}
import javax.swing.{JPanel, SwingUtilities}

final class Java2DPanel[A](frame: Frame,
                           f: Algebra => Drawing[A],
                           cb: Either[Throwable, A] => Unit)
    extends JPanel {
  val drawing = f(Algebra())
  val (bb, ctx) = drawing(DrawingContext.empty)

  import Size._
  frame.size match {
    case FitToImage(b) =>
      setPreferredSize(new Dimension(bb.width.toInt + b, bb.height.toInt + b))
    case FixedSize(w, h) =>
      setPreferredSize(new Dimension(w.toInt, h.toInt))
    case FullScreen => ???
  }

  override def paintComponent(context: Graphics): Unit = {
    val gc = context.asInstanceOf[Graphics2D]
    Java2D.setup(gc)
    SwingUtilities.windowForComponent(this).pack()

    val tx = Transform.logicalToScreen(getWidth.toDouble, getHeight.toDouble)
    ctx((gc, tx))(Point.zero).map(a => cb(Right(a))).unsafeRunSync()
 }
}
