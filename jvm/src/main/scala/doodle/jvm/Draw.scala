package doodle.jvm

import java.awt.{Graphics, Graphics2D, Shape => Shape2D, Color => AwtColor, BasicStroke}
import java.awt.geom.{Ellipse2D, Rectangle2D, Path2D}
import javax.swing.{JFrame, JPanel}

import doodle.core._

object Draw {
  def apply(img: Image): Unit = {
    val canvas = new JPanel {
      override def paintComponent(ctx: Graphics): Unit = Draw.draw(
        img,
        Point(getWidth/2, getHeight/2),
        DrawingContext.blackLines,
        ctx.asInstanceOf[Graphics2D]
      )
    }

    val frame = new JFrame("Doodle")
    frame.getContentPane().add(canvas)
    frame.setSize(500, 500)
    frame.setVisible(true)
  }

  def draw(img: Image, origin: Point, context: DrawingContext, ctx: Graphics2D): Unit = {
    def awtColor(color: Color): AwtColor = {
      val RGBA(r, g, b, a) = color.toRGBA
      new AwtColor(r.get, g.get, b.get, a.toUnsignedByte.get)
    }

    def doStrokeAndFill(shape: Shape2D) = {
      context.stroke.foreach {
        case Stroke(width, color, cap, join) => {
          ctx.setStroke(new BasicStroke(width.toFloat))
          ctx.setPaint(awtColor(color))
          // TODO: cap, join
          ctx.draw(shape)
        }
      }
      context.fill.foreach {
        case Fill(color) => {
          ctx.setPaint(awtColor(color))
          ctx.fill(shape)
        }
      }
    }

    img match {
      case Circle(r) =>
        doStrokeAndFill(new Ellipse2D.Double(origin.x-r, origin.y-r, r*2, r*2))

      case Rectangle(w, h) =>
        doStrokeAndFill(new Rectangle2D.Double(origin.x - w/2, origin.y - h/2, w, h))

      case Triangle(w, h) =>
        val path = new Path2D.Double()
        path.moveTo(origin.x      , origin.y - h/2)
        path.lineTo(origin.x + w/2, origin.y + h/2)
        path.lineTo(origin.x - w/2, origin.y + h/2)
        path.lineTo(origin.x      , origin.y - h/2)
        doStrokeAndFill(path)

      case Overlay(t, b) =>
        draw(b, origin, context, ctx)
        draw(t, origin, context, ctx)

      case b @ Beside(l, r) =>
        val box = BoundingBox(b)
        val lBox = BoundingBox(l)
        val rBox = BoundingBox(r)

        val lOriginX = origin.x + box.left + (lBox.width / 2)
        val rOriginX = origin.x + box.right - (rBox.width / 2)
        // Beside always vertically centers l and r, so we don't need
        // to calculate center ys for l and r.

        draw(l, Point(lOriginX, origin.y), context, ctx)
        draw(r, Point(rOriginX, origin.y), context, ctx)
      case a @ Above(t, b) =>
        val box  = BoundingBox(a)
        val tBox = BoundingBox(t)
        val bBox = BoundingBox(b)

        val tOriginY = origin.y + box.top + (tBox.height / 2)
        val bOriginY = origin.y + box.bottom - (bBox.height / 2)

        draw(t, Point(origin.x, tOriginY), context, ctx)
        draw(b, Point(origin.x, bOriginY), context, ctx)
      case At(vec, i) =>
        draw(i, origin + vec, context, ctx) 

      case ContextTransform(f, i) =>
        draw(i, origin, f(context), ctx)
    }
  }
}
