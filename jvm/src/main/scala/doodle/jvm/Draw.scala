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
          Point(getWidth/2, getHeight/2),
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

  def draw(image: Image, origin: Point, context: DrawingContext, ctx: Graphics2D): Unit = {
    def awtColor(color: Color): AwtColor = {
      val RGBA(r, g, b, a) = color.toRGBA
      new AwtColor(r.get, g.get, b.get, a.toUnsignedByte.get)
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
        val box  = BoundingBox(b)
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
