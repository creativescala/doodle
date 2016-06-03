package doodle
package jvm

import doodle.core.{DrawingContext, Image, Point}
import doodle.backend.{Configuration, Interpreter}

import java.awt.{Dimension, Graphics, Graphics2D}
import javax.swing.{JPanel, SwingUtilities}

final class CanvasPanel(interpreter: Configuration => Interpreter, image: Image) extends JPanel {

  override def paintComponent(context: Graphics): Unit = {
    val graphics = context.asInstanceOf[Graphics2D]
    Java2D.setup(graphics)

    val metrics = Java2D.fontMetrics(graphics)
    val dc = DrawingContext.blackLines
    val renderable = interpreter((dc, metrics))(image)
    val bb = renderable.boundingBox

    this.setPreferredSize(new Dimension(bb.width.toInt + 40, bb.height.toInt + 40))
    SwingUtilities.windowForComponent(this).pack()

    val screenCenter = Point.cartesian(getWidth / 2, getHeight / 2)

    Java2D.draw(graphics, screenCenter, dc, renderable)
  }
}
