package doodle

import org.scalajs.dom

object Draw {
  def apply(img: Image, canvas: dom.HTMLCanvasElement): Unit = {
    val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
    val originX  = canvas.width / 2
    val originY = canvas.height / 2

    draw(img, originX, originY, DrawingContext.blackLines, ctx)
  }

  def draw(img: Image, originX: Double, originY: Double, context: DrawingContext, ctx: dom.CanvasRenderingContext2D): Unit = {
    def doStrokeAndFill() = {
      context.stroke.foreach {
        case Stroke(width, colour, cap, join) => {
          ctx.lineWidth = width
          ctx.lineCap = cap.toCanvas
          ctx.lineJoin = join.toCanvas
          ctx.strokeStyle = colour.toCanvas
          ctx.stroke()
        }
      }
      context.fill.foreach {
        case Fill(colour) => {
          ctx.fillStyle = colour.toCanvas
          ctx.fill()
        }
      }
    }

    img match {
      case Circle(r) =>
        ctx.beginPath()
        ctx.arc(originX, originY, r, 0.0, Math.PI * 2)
        ctx.closePath()
        doStrokeAndFill()
      case Rectangle(w, h) =>
        ctx.beginPath()
        ctx.rect(originX - w/2, originY - h/2, w, h)
        ctx.closePath()
        doStrokeAndFill()
      case Triangle(w, h) =>
        ctx.beginPath()
        ctx.moveTo(originX      , originY - h/2)
        ctx.lineTo(originX + w/2, originY + h/2)
        ctx.lineTo(originX - w/2, originY + h/2)
        ctx.closePath()
        doStrokeAndFill()

      case Overlay(t, b) =>
        draw(b, originX, originY, context, ctx)
        draw(t, originX, originY, context, ctx)
      case b @ Beside(l, r) =>
        val box = b.boundingBox
        val lBox = l.boundingBox
        val rBox = r.boundingBox

        val lOriginX = originX + box.left + (lBox.width / 2)
        val rOriginX = originX + box.right - (rBox.width / 2)
        // Beside always vertically centers l and r, so we don't need
        // to calculate center ys for l and r.

        draw(l, lOriginX, originY, context, ctx)
        draw(r, rOriginX, originY, context, ctx)
      case a @ Above(t, b) =>
        val box = a.boundingBox
        val tBox = t.boundingBox
        val bBox = b.boundingBox

        val tOriginY = originY + box.top + (tBox.height / 2)
        val bOriginY = originY + box.bottom - (bBox.height / 2)

        draw(t, originX, tOriginY, context, ctx)
        draw(b, originX, bOriginY, context, ctx)

      case ContextTransform(f, i) =>
        draw(i, originX, originY, f(context), ctx)
    }
  }
}
