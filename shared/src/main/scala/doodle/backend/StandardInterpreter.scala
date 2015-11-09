package doodle
package backend

import doodle.core._

/**
  *  The standard interpreter that renders an Image to a Canvas. No special
  *  effects or transformations are applied; hence this is the default or
  *  standard interpreter.
  */
trait StandardInterpreter extends Interpreter {
  def draw(image: Image, canvas: Canvas, context: DrawingContext, origin: Vec): Unit = {
    def doStrokeAndFill() = {
      context.fill.foreach { fill =>
        canvas.setFill(fill.color)
        canvas.fill()
      }

      context.stroke.foreach { stroke =>
        canvas.setStroke(stroke)
        canvas.stroke()
      }
    }

    image match {
      case Path(elts) =>
        canvas.beginPath()
        canvas.moveTo(origin.x, origin.y)

        elts.foreach {
          case MoveTo(Vec(x, y)) =>
            canvas.moveTo(origin.x + x, origin.y + y)

          case LineTo(Vec(x, y)) =>
            canvas.lineTo(origin.x + x, origin.y + y)

          case BezierCurveTo(Vec(cp1x, cp1y), Vec(cp2x, cp2y), Vec(x, y)) =>
            canvas.bezierCurveTo(
              origin.x + cp1x , origin.y + cp1y,
              origin.x + cp2x , origin.y + cp2y,
              origin.x + x    , origin.y + y
            )
        }
        canvas.endPath()
        doStrokeAndFill()

      case Circle(r) =>
        canvas.circle(origin.x, origin.y, r)
        doStrokeAndFill()

      case Rectangle(w, h) =>
        canvas.rectangle(origin.x - w/2, origin.y + h/2, w, h)
        doStrokeAndFill()

      case Triangle(w, h) =>
        canvas.beginPath()
        canvas.moveTo(origin.x      , origin.y + h/2)
        canvas.lineTo(origin.x + w/2, origin.y - h/2)
        canvas.lineTo(origin.x - w/2, origin.y - h/2)
        canvas.lineTo(origin.x      , origin.y + h/2)
        canvas.endPath()
        doStrokeAndFill()

      case On(t, b) =>
        draw(b, canvas, context, origin)
        draw(t, canvas, context, origin)

      case b @ Beside(l, r) =>
        val box = b.boundingBox
        val lBox = l.boundingBox
        val rBox = r.boundingBox

        val lOriginX = origin.x + box.left  + (lBox.width / 2)
        val rOriginX = origin.x + box.right - (rBox.width / 2)
        // Beside always vertically centers l and r, so we don't need
        // to calculate center ys for l and r.

        draw(l, canvas, context, Vec(lOriginX, origin.y))
        draw(r, canvas, context, Vec(rOriginX, origin.y))
      case a @ Above(t, b) =>
        val box = a.boundingBox
        val tBox = t.boundingBox
        val bBox = b.boundingBox

        val tOriginY = origin.y + box.top - (tBox.height / 2)
        val bOriginY = origin.y + box.bottom + (bBox.height / 2)

        draw(t, canvas, context, Vec(origin.x, tOriginY))
        draw(b, canvas, context, Vec(origin.x, bOriginY))
      case At(vec, i) =>
        draw(i, canvas, context, origin + vec)

      case ContextTransform(f, i) =>
        draw(i, canvas, f(context), origin)

      case Empty =>
    }
  }
}

object StandardInterpreter {
  implicit object interpreter extends StandardInterpreter
}
