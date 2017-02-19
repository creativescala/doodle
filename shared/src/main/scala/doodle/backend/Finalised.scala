package doodle
package backend

import doodle.core.{Image, DrawingContext, Point, PathElement}
import doodle.core.transform

/**
  * A Finalised object has a DrawingContext associated with every leaf node in the tree. This meanas we can calculate the dimensions of every element and hence lay it out.
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
    import Trampoline._

    def binaryStep(
      first: Image,
      second: Image,
      context: DrawingContext,
      create: (Finalised, Finalised) => Finalised,
      cont: Finalised => Trampoline[Finalised]): Trampoline[Finalised] =
    {
      val k = (a: Finalised) => {
        continue {
            val k = (b: Finalised) => { cont(create(a, b)) }

            step(second, context)(k)
          }
      }

      continue(step(first, context)(k))
    }

    def step(image: Image,
             context: DrawingContext)
             (cont: Finalised => Trampoline[Finalised]): Trampoline[Finalised] =
      image match {
        case Image.Empty =>
          continue(cont(Empty))

        case Image.OpenPath(elts) =>
          continue(cont(OpenPath(context, elts)))

        case Image.ClosedPath(elts) =>
          continue(cont(ClosedPath(context, elts)))

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
          continue(cont(ClosedPath(context, elts)))

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
          continue(cont(ClosedPath(context, elts)))

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
          continue(cont(ClosedPath(context, elts)))

        case Image.Text(txt) =>
          continue(cont(Text(context, txt)))

        case Image.Beside(l, r) =>
          binaryStep(l, r, context, Beside.apply _, cont)

        case Image.Above(t, b) =>
          binaryStep(t, b, context, Above.apply _, cont)

        case Image.On(o, u) =>
          binaryStep(o, u, context, On.apply _, cont)

        case Image.Transform(tx, i) =>
          continue(step(i, context){ (i: Finalised) =>
                     continue(cont(Transform(tx, i)))})

        case Image.ContextTransform(f, i) =>
          continue(step(i, f(context))(cont))
      }

    step(image, context)(i => stop(i)).value
  }
}
