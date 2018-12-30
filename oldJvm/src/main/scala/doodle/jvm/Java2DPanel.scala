package doodle
package jvm

import doodle.core.{DrawingContext, Image, Point}
import doodle.backend.{Finaliser, Renderer}
import java.util.concurrent.ConcurrentLinkedQueue
import java.awt.{Dimension, Graphics, Graphics2D}
import javax.swing.{JPanel, SwingUtilities}

final class Java2DPanel(finaliser: Finaliser, renderer: Renderer) extends JPanel {

  val imageQueue = new ConcurrentLinkedQueue[Image]

  override def paintComponent(context: Graphics): Unit = {
    val graphics = context.asInstanceOf[Graphics2D]
    Java2D.setup(graphics)

    val metrics = Java2D.fontMetrics(graphics)
    val dc = DrawingContext.blackLines
    val configuration = (dc, metrics)

    val iterator = imageQueue.iterator
    while(iterator.hasNext) {
      val img = iterator.next()

      val finalised = finaliser.run(configuration)(img)
      val bb = finalised.boundingBox

      this.setPreferredSize(new Dimension(bb.width.toInt + 40, bb.height.toInt + 40))
      SwingUtilities.windowForComponent(this).pack()
      val screenCenter = Point.cartesian(getWidth / 2, getHeight / 2)

      val canvas = new Java2DCanvas(graphics, bb.center, screenCenter)
      renderer.run(canvas)(finalised)
    }
  }
}
