package doodle
package backend

import doodle.core.{DrawingContext,PathElement,MoveTo,LineTo,BezierCurveTo,Point,Vec}

/**
  * This Image representation serves as the intermediate form used for
  * compilation of `core.Image` into a form that can rendered. It's main use is
  * to associate a `DrawingContext` with every path in the graph. This allows us
  * to calculate the true size of the bounding box taking into account the line
  * width.
  */
sealed abstract class Image extends Product with Serializable {
  lazy val boundingBox: BoundingBox =
    this match {
      case Path(ctx, elts) =>
        val bb = BoundingBox(
          elts.flatMap {
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
        ctx.lineWidth.map(w => bb.pad(w / 2)).getOrElse(bb)
      case Beside(l, r) =>
        l.boundingBox beside r.boundingBox
      case Above(t, b) =>
        t.boundingBox above b.boundingBox
      case On(o, u) =>
        o.boundingBox on u.boundingBox
      case At(offset, i) =>
        i.boundingBox at offset
      case Empty =>
        BoundingBox.empty
    }
}
object Image {
  def compile(image: doodle.core.Image, context: DrawingContext): Image = {
    import doodle.core
    import Point._

    def loop(image: doodle.core.Image, context: DrawingContext): Image =
      image match {
        case core.Empty =>
          Empty

        case core.Path(elts) =>
          Path(context, elts)

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
          Path(context, elts)

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
          Path(context, elts)

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
          Path(context, elts)

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

}
final case class Path(context: DrawingContext, elements: Seq[PathElement]) extends Image
final case class Beside(l: Image, r: Image) extends Image
final case class Above(t: Image, b: Image) extends Image
final case class On(o: Image, u: Image) extends Image
final case class At(offset: Vec, i: Image) extends Image
final case object Empty extends Image 
