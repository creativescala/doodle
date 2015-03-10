package doodle.jvm

import doodle.core._

import java.awt.{Color => AwtColor, BasicStroke, Dimension, Graphics, Graphics2D, RenderingHints, Shape}
import java.awt.geom.{Ellipse2D, Path2D, Rectangle2D}
import javax.swing.JPanel

object DoodlePanel {
  def apply(image: Image): DoodlePanel =
    new DoodlePanel(image)
}

class DoodlePanel private(private var _image: Image) extends JPanel {
  updateBounds()

  def image: Image =
    _image

  def image_=(image: Image): Unit = {
    _image = image
    updateBounds()
    repaint()
  }

  private def updateBounds(): Unit = {
    val margin = 20
    val bounds = BoundingBox(image)
    setPreferredSize(new Dimension(
      bounds.width.toInt  + 2 * margin,
      bounds.height.toInt + 2 * margin
    ))
  }

  override def paintComponent(graphics: Graphics): Unit = {
    implicit val ctx = graphics.asInstanceOf[Graphics2D]

    ctx.setRenderingHints(new RenderingHints(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON
    ))

    val bounds = BoundingBox(image)
    val center = Vec(getWidth/2, getHeight/2) - bounds.center

    paintImage(image, center, DrawingContext.blackLines)
  }

  def paintImage(image: Image, origin: Vec, context: DrawingContext)(implicit g: Graphics2D): Unit = {
    def strokeAndFill(shape: Shape) = {
      context.fill.foreach {
        case Fill(color) => {
          g.setPaint(awtColor(color))
          g.fill(shape)
        }
      }

      context.stroke.foreach {
        case Stroke(width, color, cap, join) => {
          g.setStroke(new BasicStroke(width.toFloat))
          g.setPaint(awtColor(color))
          g.draw(shape)
        }
      }
    }

    image match {
      case Path(elts) =>
        val path = new Path2D.Double()
        elts foreach {
          case MoveTo(Vec(x, y)) =>
            path.moveTo(origin.x + x, origin.y + y)

          case LineTo(Vec(x, y)) =>
            path.lineTo(origin.x + x, origin.y + y)

          case BezierCurveTo(Vec(cp1x, cp1y), Vec(cp2x, cp2y), Vec(x, y)) =>
            path.curveTo(
              origin.x + cp1x , origin.y + cp1y,
              origin.x + cp2x , origin.y + cp2y,
              origin.x + x    , origin.y + y
            )
        }
        strokeAndFill(path)

      case Circle(r) =>
        strokeAndFill(new Ellipse2D.Double(origin.x-r, origin.y-r, r*2, r*2))

      case Rectangle(w, h) =>
        strokeAndFill(new Rectangle2D.Double(origin.x-w/2, origin.y-h/2, w, h))

      case Triangle(w, h) =>
        val path = new Path2D.Double()
        path.moveTo(origin.x       , origin.y - h/2)
        path.lineTo(origin.x + w/2 , origin.y + h/2)
        path.lineTo(origin.x - w/2 , origin.y + h/2)
        path.lineTo(origin.x       , origin.y - h/2)
        path.closePath()
        strokeAndFill(path)

      case Overlay(t, b) =>
        paintImage(b, origin, context)
        paintImage(t, origin, context)

      case b @ Beside(l, r) =>
        val box  = BoundingBox(b)
        val lBox = BoundingBox(l)
        val rBox = BoundingBox(r)

        val lOriginX = origin.x + box.left  + (lBox.width / 2)
        val rOriginX = origin.x + box.right - (rBox.width / 2)
        // Beside always vertically centers l and r, so we don't need
        // to calculate center ys for l and r.

        paintImage(l, Vec(lOriginX, origin.y), context)
        paintImage(r, Vec(rOriginX, origin.y), context)
      case a @ Above(t, b) =>
        val box  = BoundingBox(a)
        val tBox = BoundingBox(t)
        val bBox = BoundingBox(b)

        val tOriginY = origin.y + box.top + (tBox.height / 2)
        val bOriginY = origin.y + box.bottom - (bBox.height / 2)

        paintImage(t, Vec(origin.x, tOriginY), context)
        paintImage(b, Vec(origin.x, bOriginY), context)
      case At(vec, i) =>
        paintImage(i, origin + vec, context)

      case ContextTransform(f, i) =>
        paintImage(i, origin, f(context))

      case d: Drawable =>
        paintImage(d.draw, origin, context)
    }
  }

  private def awtColor(color: Color): AwtColor = {
    val RGBA(r, g, b, a) = color.toRGBA
    new AwtColor(r.get, g.get, b.get, a.toUnsignedByte.get)
  }
}
