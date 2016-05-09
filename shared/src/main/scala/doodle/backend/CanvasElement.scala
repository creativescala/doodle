package doodle
package backend

import doodle.core.{DrawingContext,PathElement,Vec}

sealed abstract class CanvasElement extends Product with Serializable
object CanvasElement {
  final case class OpenPath(context: DrawingContext, at: Vec, elements: List[PathElement]) extends CanvasElement
  final case class ClosedPath(context: DrawingContext, at: Vec, elements: List[PathElement]) extends CanvasElement
  final case class Text(context: DrawingContext, at: Vec, boundingBox: BoundingBox, characters: String) extends CanvasElement
  final case object Empty extends CanvasElement
}
