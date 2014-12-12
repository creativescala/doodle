package doodle

import org.scalajs.dom

object Draw {
  def apply(img: Image, canvas: dom.HTMLCanvasElement): Unit = {
    val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
    val originX  = canvas.width / 2
    val originY = canvas.height / 2

    draw(img, originX, originY, StrokeStyle(Line(1.0), RGB(0, 0, 0)), Solid(RGB(0, 0, 0)), ctx)
  }

  def draw(img: Image, originX: Double, originY: Double, stroke: StrokeStyle, fill: FillStyle, ctx: dom.CanvasRenderingContext2D): Unit = {
    def doStrokeAndFill() = {
      if(stroke.line.width > 0.0) {
        ctx.lineWidth = stroke.line.width
        ctx.lineCap = stroke.line.cap.toCanvas
        ctx.lineJoin = stroke.line.join.toCanvas

        ctx.strokeStyle = stroke.colour.toCanvas
        ctx.stroke()
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
<<<<<<< HEAD:src/main/scala/doodle/Draw.scala

=======
      case Triangle(w, h) =>
        ctx.beginPath()
        ctx.moveTo(originX      , originY - h/2)
        ctx.lineTo(originX + w/2, originY + h/2)
        ctx.lineTo(originX - w/2, originY + h/2)
        ctx.closePath()
        doStrokeAndFill()
>>>>>>> 3e85d6bbc7c99f0190ea1ad808ed72736e93f4bf:src/main/scala/doodle/Scene.scala
      case Overlay(t, b) =>
        draw(b, originX, originY, stroke, fill, ctx)
        draw(t, originX, originY, stroke, fill, ctx)
      case b @ Beside(l, r) =>
        val box = b.boundingBox
        val lBox = l.boundingBox
        val rBox = r.boundingBox

        val lOriginX = originX + box.left + (lBox.width / 2)
        val rOriginX = originX + box.right - (rBox.width / 2)
        // Beside always vertically centers l and r, so we don't need
        // to calculate center ys for l and r.

        draw(l, lOriginX, originY, stroke, fill, ctx)
        draw(r, rOriginX, originY, stroke, fill, ctx)
      case a @ Above(t, b) =>
        val box = a.boundingBox
        val tBox = t.boundingBox
        val bBox = b.boundingBox

        val tOriginY = originY + box.top + (tBox.height / 2)
        val bOriginY = originY + box.bottom - (bBox.height / 2)

        draw(t, originX, tOriginY, stroke, fill, ctx)
        draw(b, originX, bOriginY, stroke, fill, ctx)

      case StrokeColour(c, i) =>
        val newStroke = stroke.copy(colour = c)
        draw(i, originX, originY, newStroke, fill, ctx)
      case StrokeWidth(w, i) =>
        val newStroke = stroke.copy(line = stroke.line.copy(width = w))
        draw(i, originX, originY, newStroke, fill, ctx)
    }
  }
}
