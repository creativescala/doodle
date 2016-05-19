package doodle
package backend

import doodle.core.{Image, DrawingContext, BezierCurveTo, LineTo, MoveTo, Point, PathElement, Vec}

/**
  *  The standard interpreter that renders an Image. No special effects or
  *  transformations are applied; hence this is the default or standard
  *  interpreter.
  */
final case class StandardInterpreter(context: DrawingContext, metrics: Metrics) {
  def interpret(image: Image): Renderable =
    layout(finalise(image))

  def finalise(image: Image): Finalised = {
    import doodle.core
    import Point._
    import Finalised._

    def loop(image: Image, context: DrawingContext): Finalised =
      image match {
        case core.Empty =>
          Empty

        case core.OpenPath(elts) =>
          OpenPath(context, elts)

        case core.ClosedPath(elts) =>
          ClosedPath(context, elts)

        case core.Circle(r) =>
          // See http://spencermortensen.com/articles/bezier-circle/ for approximation
          // of a circle with a Bezier curve.
          val c = 0.551915024494
          val cR = c * r
          val elts = List(
            MoveTo(cartesian(0.0, r)),
            BezierCurveTo(cartesian(cR, r), cartesian(r, cR), cartesian(r, 0.0)),
            BezierCurveTo(cartesian(r, -cR), cartesian(cR, -r), cartesian(0.0, -r)),
            BezierCurveTo(cartesian(-cR, -r), cartesian(-r, -cR), cartesian(-r, 0.0)),
            BezierCurveTo(cartesian(-r, cR), cartesian(-cR, r), cartesian(0.0, r))
          )
          ClosedPath(context, elts)

        case core.Rectangle(w, h) =>
          val left = -w/2
          val top = h/2
          val right = w/2
          val bottom = -h/2
          val elts = List(
            MoveTo(cartesian(left, top)),
            LineTo(cartesian(right, top)),
            LineTo(cartesian(right, bottom)),
            LineTo(cartesian(left, bottom)),
            LineTo(cartesian(left, top))
          )
          ClosedPath(context, elts)

        case core.Triangle(w, h) =>
          val left = -w/2
          val top = h/2
          val right = w/2
          val bottom = -h/2

          val elts = List(
            MoveTo(cartesian(left, bottom)),
            LineTo(cartesian(0.0, top)),
            LineTo(cartesian(right, bottom)),
            LineTo(cartesian(left, bottom))
          )
          ClosedPath(context, elts)

        case core.Text(txt) =>
          Text(context, txt)

        case core.Beside(l, r) =>
          Beside(loop(l, context), loop(r, context))

        case core.Above(t, b) =>
          Above(loop(t, context), loop(b, context))

        case core.On(o, u) =>
          On(loop(o, context), loop(u, context))

        case core.At(offset, i) =>
          At(offset, loop(i, context))

        case core.ContextTransform(f, i) =>
          loop(i, f(context))
      }

    loop(image, context)
  }

  def layout(finalised: Finalised): Renderable = {
    import Finalised._

    def loop(finalised: Finalised, origin: Point): List[CanvasElement] =
      finalised match {
        case ClosedPath(ctx, elts) =>
          List(CanvasElement.ClosedPath(ctx, origin.toVec, elts.toList))

        case OpenPath(ctx, elts) =>
          List(CanvasElement.OpenPath(ctx, origin.toVec, elts.toList))

        case t @ Text(ctx, txt) =>
          List(CanvasElement.Text(ctx, origin.toVec, boundingBox(t), txt))

        case On(t, b) =>
          loop(b, origin) ++ loop(t, origin)

        case b @ Beside(l, r) =>
          val box = boundingBox(b)
          val lBox = boundingBox(l)
          val rBox = boundingBox(r)

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

          loop(l, lOrigin) ++ loop(r, rOrigin)

        case a @ Above(t, b) =>
          val box = boundingBox(a)
          val tBox = boundingBox(t)
          val bBox = boundingBox(b)

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

          loop(t, tOrigin) ++ loop(b, bOrigin)

        case At(vec, i) =>
          loop(i, origin + vec)

        case Empty =>
          List.empty[CanvasElement]
      }

    Renderable(boundingBox(finalised), loop(finalised, Point.zero))
  }

  def boundingBox(finalised: Finalised): BoundingBox = {
    import Finalised._

    def pathElementsToBoundingBox(elts: Seq[PathElement]): BoundingBox =
      BoundingBox(
        Point.zero +: elts.flatMap {
          case MoveTo(pos) => Seq(pos)
          case LineTo(pos) => Seq(pos)
          case BezierCurveTo(cp1, cp2, pos) =>
            // The control points form a bounding box around a bezier curve,
            // but this may not be a tight bounding box.
            // It's an acceptable solution for now but in the future
            // we may wish to generate a tighter bounding box.
            Seq(cp1, cp2, pos)
        }
      )

    finalised match {
      case OpenPath(ctx, elts) =>
        val bb = pathElementsToBoundingBox(elts)
        ctx.lineWidth.map(w => bb.pad(w / 2)).getOrElse(bb)
      case ClosedPath(ctx, elts) =>
        val bb = pathElementsToBoundingBox(elts)
        ctx.lineWidth.map(w => bb.pad(w / 2)).getOrElse(bb)
      case Text(ctx, txt) =>
        ctx.font.map(f => metrics(f, txt)).getOrElse(BoundingBox.empty)
      case Beside(l, r) =>
        boundingBox(l) beside boundingBox(r)
      case Above(t, b) =>
        boundingBox(t) above boundingBox(b)
      case On(o, u) =>
        boundingBox(o) on boundingBox(u)
      case At(offset, i) =>
        boundingBox(i) at offset
      case Empty =>
        BoundingBox.empty
    }
  }
}
object StandardInterpreter {
  implicit val interpreter: Configuration => Interpreter = {
    case (dc, metrics) => img => StandardInterpreter(dc, metrics).interpret(img)
  }
}


/**
  * A Finalised object has a DrawingContext associated with every leaf node in
  * the tree. This meanas we can calculate the dimensions of every element and
  * hence lay it out.
  */
sealed abstract class Finalised extends Product with Serializable 
object Finalised {
  final case class OpenPath(context: DrawingContext, elements: Seq[PathElement]) extends Finalised
  final case class ClosedPath(context: DrawingContext, elements: Seq[PathElement]) extends Finalised
  final case class Text(context: DrawingContext, characters: String) extends Finalised
  final case class Beside(l: Finalised, r: Finalised) extends Finalised
  final case class Above(t: Finalised, b: Finalised) extends Finalised
  final case class On(o: Finalised, u: Finalised) extends Finalised
  final case class At(offset: Vec, i: Finalised) extends Finalised
  final case object Empty extends Finalised
}
