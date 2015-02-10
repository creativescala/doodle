package doodle.js

import org.scalajs.dom

import doodle.core._

object Draw {
  def apply(img: Image, id: String): Unit = {
    apply(img, dom.document.getElementById("canvas").asInstanceOf[dom.HTMLCanvasElement])
  }

  def apply(img: Image, canvas: dom.HTMLCanvasElement): Unit = {
    val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
    val originX = canvas.width / 2
    val originY = canvas.height / 2

    draw(img, Point(originX, originY), DrawingContext.blackLines, ctx)
  }

  def draw(img: Image, origin: Point, context: DrawingContext, ctx: dom.CanvasRenderingContext2D): Unit = {
    def doStrokeAndFill() = {
      context.stroke.foreach {
        case Stroke(width, color, cap, join) => {
          ctx.lineWidth = width
          ctx.lineCap = cap.toCanvas
          ctx.lineJoin = join.toCanvas
          ctx.strokeStyle = color.toCanvas
          ctx.stroke()
        }
      }
      context.fill.foreach {
        case Fill(color) => {
          ctx.fillStyle = color.toCanvas
          ctx.fill()
        }
      }
    }

    img match {
      case Circle(r) =>
        ctx.beginPath()
        ctx.arc(origin.x, origin.y, r, 0.0, Math.PI * 2)
        ctx.closePath()
        doStrokeAndFill()
      case Rectangle(w, h) =>
        ctx.beginPath()
        ctx.rect(origin.x - w/2, origin.y - h/2, w, h)
        ctx.closePath()
        doStrokeAndFill()
      case Triangle(w, h) =>
        ctx.beginPath()
        ctx.moveTo(origin.x      , origin.y - h/2)
        ctx.lineTo(origin.x + w/2, origin.y + h/2)
        ctx.lineTo(origin.x - w/2, origin.y + h/2)
        ctx.closePath()
        doStrokeAndFill()

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
        val box = BoundingBox(a)
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
