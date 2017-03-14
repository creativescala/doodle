package doodle
package backend

import doodle.core.{DrawingContext, PathElement}
import doodle.core.transform.Transform

/**
  * A canvas interface abstracts over the different vector drawing libraries we support. It provides a low-level imperative API, the higher-level declarative APIs can compile into.
  *
  * We implement this as an open trait (vs sealed trait) as the different implementations live in different packages and type classes don't work well with stateful interfaces.
  *
  * We assume the origin is centered in the drawing context, and coordinates follow the standard cartesian layout. The Canvas implementation is responsible for translating this coordinate system to whatever is used by the specific implementation it draws to.
  *
  * This is essentially a Church encoding of the Finalised representation, minus the layout operators. Canvas allows *absolute* layout with minimum memory allocation, and is thus more efficient but less convenient than a deep embedding such as Finalised.
  */
trait Canvas {
  def openPath(context: DrawingContext, elements: List[PathElement]): Unit
  def closedPath(context: DrawingContext, elements: List[PathElement]): Unit
  def text(context: DrawingContext, tx: Transform, boundingBox: BoundingBox, characters: String): Unit
}
