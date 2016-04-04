package doodle
package backend

import doodle.core.{DrawingContext,ContextTransform,BezierCurveTo,LineTo,MoveTo,Point,Vec}

/**
  *  The standard interpreter that renders an Image to a Canvas. No special
  *  effects or transformations are applied; hence this is the default or
  *  standard interpreter.
  */
trait StandardInterpreter extends Interpreter {
  def draw(image: Image, canvas: Canvas, origin: Point): Unit = {
    import Point.extractors.Cartesian

    image match {
      case Path(ctx, elts) =>
        canvas.beginPath()
        canvas.moveTo(origin.x, origin.y)

        elts.foreach {
          case MoveTo(Cartesian(x, y)) =>
            canvas.moveTo(origin.x + x, origin.y + y)

          case LineTo(Cartesian(x, y)) =>
            canvas.lineTo(origin.x + x, origin.y + y)

          case BezierCurveTo(Cartesian(cp1x, cp1y), Cartesian(cp2x, cp2y), Cartesian(x, y)) =>
            canvas.bezierCurveTo(
              origin.x + cp1x , origin.y + cp1y,
              origin.x + cp2x , origin.y + cp2y,
              origin.x + x    , origin.y + y
            )
        }
        canvas.endPath()
        ctx.fill.foreach { fill =>
          canvas.setFill(fill.color)
          canvas.fill()
        }
        ctx.stroke.foreach { stroke =>
          canvas.setStroke(stroke)
          canvas.stroke()
        }

      case On(t, b) =>
        draw(b, canvas, origin)
        draw(t, canvas, origin)

      case b @ Beside(l, r) =>
        val box = b.boundingBox
        val lBox = l.boundingBox
        val rBox = r.boundingBox

        val lOriginX = origin.x + box.left  + (lBox.width / 2)
        val rOriginX = origin.x + box.right - (rBox.width / 2)
        // Beside always vertically centers l and r, so we don't need
        // to calculate center ys for l and r.

        draw(l, canvas, Point.cartesian(lOriginX, origin.y))
        draw(r, canvas, Point.cartesian(rOriginX, origin.y))
      case a @ Above(t, b) =>
        val box = a.boundingBox
        val tBox = t.boundingBox
        val bBox = b.boundingBox

        val tOriginY = origin.y + box.top - (tBox.height / 2)
        val bOriginY = origin.y + box.bottom + (bBox.height / 2)

        draw(t, canvas, Point.cartesian(origin.x, tOriginY))
        draw(b, canvas, Point.cartesian(origin.x, bOriginY))
      case At(vec, i) =>
        draw(i, canvas, origin + vec)

      case Empty =>
    }
  }
}

object StandardInterpreter {
  implicit object interpreter extends StandardInterpreter
}
