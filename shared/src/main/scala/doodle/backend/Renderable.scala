package doodle
package backend

import doodle.core.{Point, PathElement}
import doodle.core.transform

final case class Renderable(boundingBox: BoundingBox, elements: List[CanvasElement])
object Renderable {
  /**
    * Convert local to global coordinates.
    */
  def layout(finalised: Finalised, metrics: Metrics): Renderable = {
    import Finalised._
    // The main loop converting local to global coordinates.
    //
    // We pass around:
    // - the current transform
    // - the offset from the current origin
    //
    // Each Transform node defines a new origin from which points under that Transform
    // will be transformed relative to.
    def loop(finalised: Finalised, origin: Point, tx: transform.Transform): List[CanvasElement] =
      finalised match {
        case ClosedPath(ctx, elts) =>
          val fullTx = transform.Transform.translate(origin.toVec) andThen tx
          List(CanvasElement.ClosedPath(ctx, elts.map(_.transform(fullTx)).toList))

        case OpenPath(ctx, elts) =>
          val fullTx = transform.Transform.translate(origin.toVec) andThen tx
          List(CanvasElement.OpenPath(ctx, elts.map(_.transform(fullTx)).toList))

        case t @ Text(ctx, txt) =>
          val fullTx = transform.Transform.translate(origin.toVec) andThen tx
          List(CanvasElement.Text(ctx, fullTx, boundingBox(t, metrics), txt))

        case On(t, b) =>
          loop(b, origin, tx) ++ loop(t, origin, tx)

        case b @ Beside(l, r) =>
          val box = boundingBox(b, metrics)
          val lBox = boundingBox(l, metrics)
          val rBox = boundingBox(r, metrics)

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
          loop(l, lOrigin, tx) ++ loop(r, rOrigin, tx)

        case a @ Above(t, b) =>
          val box = boundingBox(a, metrics)
          val tBox = boundingBox(t, metrics)
          val bBox = boundingBox(b, metrics)

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

          loop(t, tOrigin, tx) ++ loop(b, bOrigin, tx)

        case Transform(newTx, i) =>
          // Translate around the local origin, not the global one
          val there = transform.Transform.translate(origin.toVec)
          val fullTx =
             newTx andThen there andThen tx

          loop(i, Point.zero, fullTx)

        case Empty =>
          List.empty[CanvasElement]
      }

    Renderable(boundingBox(finalised, metrics),
               loop(finalised, Point.zero, transform.Transform.identity))
  }

  def boundingBox(finalised: Finalised, metrics: Metrics): BoundingBox = {
    import PathElement._
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
        boundingBox(l, metrics) beside boundingBox(r, metrics)
      case Above(t, b) =>
        boundingBox(t, metrics) above boundingBox(b, metrics)
      case On(o, u) =>
        boundingBox(o, metrics) on boundingBox(u, metrics)
      case Transform(tx, i) =>
        boundingBox(i, metrics).transform(tx)
      case Empty =>
        BoundingBox.empty
    }
  }
}
