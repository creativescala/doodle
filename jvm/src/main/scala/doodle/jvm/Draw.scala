package doodle.jvm

import java.awt.{
  BasicStroke,
  Color => AwtColor,
  Dimension,
  Graphics,
  Graphics2D,
  RenderingHints,
  Shape => Shape2D
}

import java.awt.geom.{
  Ellipse2D,
  Path2D,
  Rectangle2D
}

import javax.swing.{
  JFrame,
  JPanel,
  WindowConstants
}

import doodle.core._

object Draw {
  val margin = 20

  def apply(image: Image): Unit = {
    val canvas = new JPanel {
      override def paintComponent(ctx: Graphics): Unit = {
        Draw.draw(
          image,
          getWidth  / 2,
          getHeight / 2,
          DrawingContext.blackLines,
          ctx.asInstanceOf[Graphics2D]
        )
      }
    }

    val box = BoundingBox(image)
    canvas.setPreferredSize(new Dimension(
      box.width.toInt + 2 * margin,
      box.height.toInt + 2 * margin
    ))

    val frame = new JFrame("Doodle")
    // frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
    frame.getContentPane.add(canvas)
    frame.pack
    frame.setVisible(true)
  }

  def draw(image: Image, originX: Double, originY: Double, context: DrawingContext, ctx: Graphics2D): Unit = {
    def awtColor(color: Color): AwtColor = {
      val RGBA(r, g, b, a) = color.toRGBA
      new AwtColor(r.get, g.get, b.get, (a.get * 255).toInt)
    }

    def doStrokeAndFill(shape: Shape2D) = {
      ctx.setRenderingHints(new RenderingHints(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON
      ))

      context.fill.foreach {
        case Fill(color) => {
          ctx.setPaint(awtColor(color))
          ctx.fill(shape)
        }
      }

      context.stroke.foreach {
        case Stroke(width, color, cap, join) => {
          ctx.setStroke(new BasicStroke(width.toFloat))
          ctx.setPaint(awtColor(color))
          // TODO: cap, join
          ctx.draw(shape)
        }
      }
    }

    image match {
      case Circle(r) =>
        doStrokeAndFill(new Ellipse2D.Double(originX-r, originY-r, r*2, r*2))

      case Rectangle(w, h) =>
        doStrokeAndFill(new Rectangle2D.Double(originX - w/2, originY - h/2, w, h))

      case Triangle(w, h) =>
        val path = new Path2D.Double()
        path.moveTo(originX      , originY - h/2)
        path.lineTo(originX + w/2, originY + h/2)
        path.lineTo(originX - w/2, originY + h/2)
        path.lineTo(originX      , originY - h/2)
        doStrokeAndFill(path)

      case Overlay(t, b) =>
        draw(b, originX, originY, context, ctx)
        draw(t, originX, originY, context, ctx)

      case b @ Beside(l, r) =>
        val box  = BoundingBox(b)
        val lBox = BoundingBox(l)
        val rBox = BoundingBox(r)

        val lOriginX = originX + box.left + (lBox.width / 2)
        val rOriginX = originX + box.right - (rBox.width / 2)
        // Beside always vertically centers l and r, so we don't need
        // to calculate center ys for l and r.

        draw(l, lOriginX, originY, context, ctx)
        draw(r, rOriginX, originY, context, ctx)
      case a @ Above(t, b) =>
        val box  = BoundingBox(a)
        val tBox = BoundingBox(t)
        val bBox = BoundingBox(b)

        val tOriginY = originY + box.top + (tBox.height / 2)
        val bOriginY = originY + box.bottom - (bBox.height / 2)

        draw(t, originX, tOriginY, context, ctx)
        draw(b, originX, bOriginY, context, ctx)

      case ContextTransform(f, i) =>
        draw(i, originX, originY, f(context), ctx)
    }
  }
}
