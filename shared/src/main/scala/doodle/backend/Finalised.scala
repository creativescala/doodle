package doodle
package backend

import doodle.core.{Image, DrawingContext, Point, PathElement}
import doodle.core.transform
import scala.annotation.tailrec

/**
  * A Finalised object has a DrawingContext associated with every leaf node in the tree. This meanas we can calculate the dimensions of every element and hence lay it out.
  */
sealed abstract class Finalised extends Product with Serializable {
  def boundingBox: BoundingBox
}
object Finalised {
  final case class OpenPath(context: DrawingContext, elements: List[PathElement], boundingBox: BoundingBox) extends Finalised
  final case class ClosedPath(context: DrawingContext, elements: List[PathElement], boundingBox: BoundingBox) extends Finalised
  final case class Text(context: DrawingContext, characters: String, boundingBox: BoundingBox) extends Finalised
  final case class Beside(l: Finalised, r: Finalised) extends Finalised {
    val boundingBox = l.boundingBox beside r.boundingBox
  }
  final case class Above(t: Finalised, b: Finalised) extends Finalised {
    val boundingBox = t.boundingBox above b.boundingBox
  }
  final case class On(o: Finalised, u: Finalised) extends Finalised {
    val boundingBox = o.boundingBox on u.boundingBox
  }
  final case class Transform(tx: transform.Transform, i: Finalised) extends Finalised {
    val boundingBox = i.boundingBox.transform(tx)
  }
  final case object Empty extends Finalised {
    val boundingBox = BoundingBox.empty
  }

  def pathElementsToBoundingBox(elts: Seq[PathElement]): BoundingBox = {
    import PathElement._

    // This implementation should not allocate
    var minX: Double = 0.0
    var minY: Double = 0.0
    var maxX: Double = 0.0
    var maxY: Double = 0.0

    @tailrec
    def iter(elts: Seq[PathElement]): Unit =
      elts match {
        case hd +: tl =>
          hd match {
            case MoveTo(pos) =>
              minX = pos.x min minX
              minY = pos.y min minY
              maxX = pos.x max maxX
              maxY = pos.y max maxY
            case LineTo(pos) =>
              minX = pos.x min minX
              minY = pos.y min minY
              maxX = pos.x max maxX
              maxY = pos.y max maxY
            case BezierCurveTo(cp1, cp2, pos) =>
              // The control points form a bounding box around a bezier curve,
              // but this may not be a tight bounding box.
              // It's an acceptable solution for now but in the future
              // we may wish to generate a tighter bounding box.
              minX = pos.x min cp2.x min cp1.x min minX
              minY = pos.y min cp2.y min cp1.y min minY
              maxX = pos.x max cp2.x max cp1.x max maxX
              maxY = pos.y max cp2.y max cp1.y max maxY
          }
          iter(tl)
        case Seq() => ()
      }
    iter(elts)

    BoundingBox(minX, maxY, maxX, minY)
  }

  def finalise(image: Image, context: DrawingContext, metrics: Metrics): Finalised = {
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
          val bb = {
            val bb = pathElementsToBoundingBox(elts)
            context.lineWidth.map(w => bb.pad(w / 2)).getOrElse(bb)
          }
          continue(cont(OpenPath(context, elts, bb)))

        case Image.ClosedPath(elts) =>
          val bb = {
            val bb = pathElementsToBoundingBox(elts)
            context.lineWidth.map(w => bb.pad(w / 2)).getOrElse(bb)
          }
          continue(cont(ClosedPath(context, elts, bb)))

        case Image.Circle(r) =>
          // See http://spencermortensen.com/articles/bezier-circle/ for approximation of a circle with a Bezier curve.
          val c = 0.551915024494
          val cR = c * r
          val elts = List(
            MoveTo(cartesian(0.0, r)),
            BezierCurveTo(cartesian(cR, r), cartesian(r, cR), cartesian(r, 0.0)),
            BezierCurveTo(cartesian(r, -cR), cartesian(cR, -r), cartesian(0.0, -r)),
            BezierCurveTo(cartesian(-cR, -r), cartesian(-r, -cR), cartesian(-r, 0.0)),
            BezierCurveTo(cartesian(-r, cR), cartesian(-cR, r), cartesian(0.0, r))
          )
          val bb = {
            val bb = pathElementsToBoundingBox(elts)
            context.lineWidth.map(w => bb.pad(w / 2)).getOrElse(bb)
          }
          continue(cont(ClosedPath(context, elts, bb)))

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
          val bb = {
            val bb = pathElementsToBoundingBox(elts)
            context.lineWidth.map(w => bb.pad(w / 2)).getOrElse(bb)
          }
          continue(cont(ClosedPath(context, elts, bb)))

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
          val bb = {
            val bb = pathElementsToBoundingBox(elts)
            context.lineWidth.map(w => bb.pad(w / 2)).getOrElse(bb)
          }
          continue(cont(ClosedPath(context, elts, bb)))

        case Image.Text(txt) =>
          val bb =
            context.font.map(f => metrics(f, txt)).getOrElse(BoundingBox.empty)
          continue(cont(Text(context, txt, bb)))

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
