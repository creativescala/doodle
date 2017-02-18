package doodle
package backend

import doodle.core.{DrawingContext,PathElement}
import doodle.core.transform.Transform

sealed abstract class CanvasElement extends Product with Serializable
object CanvasElement {
  final case class OpenPath(context: DrawingContext, elements: List[PathElement]) extends CanvasElement
  final case class ClosedPath(context: DrawingContext, elements: List[PathElement]) extends CanvasElement
  final case class Text(context: DrawingContext, tx: Transform, boundingBox: BoundingBox, characters: String) extends CanvasElement
  final case object Empty extends CanvasElement
}
