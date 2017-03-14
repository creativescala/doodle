package doodle
package backend

import doodle.core.Point
import doodle.core.transform

object Render {
  /**
    * Convert local to global coordinates.
    */
  def render(canvas: Canvas, finalised: Finalised): Unit = {
    import Finalised._
    import Trampoline._

    // The main loop converting local to global coordinates.
    //
    // We pass around:
    // - the current transform
    // - the offset from the current origin
    //
    // Each Transform node defines a new origin from which points under that Transform will be transformed relative to.
    def step(finalised: Finalised,
             origin: Point,
             tx: transform.Transform)
            (cont: () => Trampoline[Unit]): Trampoline[Unit] =
      finalised match {
        case ClosedPath(ctx, elts, bb) =>
          val fullTx = transform.Transform.translate(origin.toVec) andThen tx
          canvas.closedPath(ctx, elts.map{ _.transform(fullTx) })

          cont()

        case OpenPath(ctx, elts, bb) =>
          val fullTx = transform.Transform.translate(origin.toVec) andThen tx
          canvas.openPath(ctx, elts.map{ _.transform(fullTx) })

          cont()

        case t @ Text(ctx, txt, bb) =>
          val fullTx = transform.Transform.translate(origin.toVec) andThen tx
          canvas.text(ctx, fullTx, t.boundingBox, txt)

          cont()

        case On(t, b) =>
          continue(step(b, origin, tx){ () =>
                     continue(step(t, origin, tx){ () =>
                                cont()
                              })
                   })

        case b @ Beside(l, r) =>
          val box = b.boundingBox
          val lBox = l.boundingBox
          val rBox = r.boundingBox

          // Beside aligns the y coordinate of the origin of the bounding boxes of l and r. We need to calculate the x coordinate of the origin of each bounding box, remembering that the origin may not be the center of the box. We first calculate the the x coordinate of the center of the l and r bounding boxes and then displace the centers to their respective origins

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
          continue(step(l, lOrigin, tx){ () =>
                     continue(step(r, rOrigin, tx){ () =>
                                cont()
                              })
                   })

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

          continue(step(t, tOrigin, tx){ () =>
                     continue(step(b, bOrigin, tx){ () =>
                                cont()
                              })
                   })

        case Transform(newTx, i) =>
          // Translate around the local origin, not the global one
          val there = transform.Transform.translate(origin.toVec)
          val fullTx =
             newTx andThen there andThen tx

          continue(step(i, Point.zero, fullTx)(cont))

        case Empty =>
          cont()
      }

    step(finalised, Point.zero, transform.Transform.identity){ () =>
      stop(())
    }.value
  }
}
