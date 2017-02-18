package doodle
package backend

import doodle.core.{Image, DrawingContext, Point, PathElement}
import doodle.core.transform

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
  final case class Transform(tx: transform.Transform, i: Finalised) extends Finalised
  final case object Empty extends Finalised


  def finalise(image: Image, context: DrawingContext): Finalised = {
    import PathElement._
    import Point._

    def loop(image: Image, context: DrawingContext): Finalised =
      image match {
        case Image.Empty =>
          Empty

        case Image.OpenPath(elts) =>
          OpenPath(context, elts)

        case Image.ClosedPath(elts) =>
          ClosedPath(context, elts)

        case Image.Circle(r) =>
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

        case Image.Rectangle(w, h) =>
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

        case Image.Triangle(w, h) =>
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

        case Image.Text(txt) =>
          Text(context, txt)

        case Image.Beside(l, r) =>
          Beside(loop(l, context), loop(r, context))

        case Image.Above(t, b) =>
          Above(loop(t, context), loop(b, context))

        case Image.On(o, u) =>
          On(loop(o, context), loop(u, context))

        case Image.Transform(tx, i) =>
          Transform(tx, loop(i, context))

        case Image.ContextTransform(f, i) =>
          loop(i, f(context))
      }

    loop(image, context)
  }
}
