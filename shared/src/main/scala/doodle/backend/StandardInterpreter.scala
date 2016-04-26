package doodle
package backend

import doodle.core.{DrawingContext,ContextTransform,BezierCurveTo,LineTo,MoveTo,Point,PathElement,Vec}

/**
  *  The standard interpreter that renders an Image to a Canvas. No special
  *  effects or transformations are applied; hence this is the default or
  *  standard interpreter.
  */
trait StandardInterpreter extends Interpreter {
  def draw(image: Image, canvas: Canvas, origin: Point): Unit = {
    import Point.extractors.Cartesian

    def drawElements(elts: Seq[PathElement]): Unit = {
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
    }

    def setContext(ctx: DrawingContext): Unit = {
      ctx.fill.foreach { fill =>
        canvas.setFill(fill.color)
        canvas.fill()
      }
      ctx.stroke.foreach { stroke =>
        canvas.setStroke(stroke)
        canvas.stroke()
      }
    }

    image match {
      case ClosedPath(ctx, elts) =>
        canvas.beginPath()
        canvas.moveTo(origin.x, origin.y)
        drawElements(elts)
        canvas.endPath()
        setContext(ctx)

      case OpenPath(ctx, elts) =>
        canvas.beginPath()
        canvas.moveTo(origin.x, origin.y)
        drawElements(elts)
        setContext(ctx)

      case On(t, b) =>
        draw(b, canvas, origin)
        draw(t, canvas, origin)

      case b @ Beside(l, r) =>
        val box = b.boundingBox
        val lBox = l.boundingBox
        val rBox = r.boundingBox

        // Beside aligns the y coordinate of the origin of the bounding boxes of
        // l and r. We need to calculate the x coordinate of the origin of each
        // bounding box, remembering that the origin may not be the center of
        // the box. We first calculate the the x coordinate of the center of the
        // l and r bounding boxes and then displace the centers to their
        // respective origins

        // The center of the l and r bounding boxes in the current coordinate system
        val lCenterX = origin.x + box.left  + (lBox.width / 2)
        val rCenterX = origin.x + box.right - (rBox.width / 2)

        // lBox and rBox may not have their origin at the center of their bounding
        // box, so we transform accordingly if need be.
        val lOrigin =
          Point.cartesian(
            lCenterX - lBox.center.x,
            origin.y
          )
        val rOrigin =
          Point.cartesian(
            rCenterX - rBox.center.x,
            origin.y
          )

        draw(l, canvas, lOrigin)
        draw(r, canvas, rOrigin)

      case a @ Above(t, b) =>
        val box = a.boundingBox
        val tBox = t.boundingBox
        val bBox = b.boundingBox

        val tCenterY = origin.y + box.top - (tBox.height / 2)
        val bCenterY = origin.y + box.bottom + (bBox.height / 2)

        val tOrigin =
          Point.cartesian(
            origin.x,
            tCenterY - tBox.center.y
          )
        val bOrigin =
          Point.cartesian(
            origin.x,
            bCenterY - bBox.center.y
          )

        draw(t, canvas, tOrigin)
        draw(b, canvas, bOrigin)

      case At(vec, i) =>
        draw(i, canvas, origin + vec)

      case Empty =>
    }
  }
}

object StandardInterpreter {
  implicit object interpreter extends StandardInterpreter
}
