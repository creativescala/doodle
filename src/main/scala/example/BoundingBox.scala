package doodle

/**
  * Represents a bounding box around an image. The coordinate system
  * follows the canvas convention, meaning that the origin is the top
  * left. However, the coordinate system is not the global coordinate
  * system of the canvast but one that is local to the image. We
  * require the image is centered at the origin, and thus left and top
  * will usually be negative.
  */
final case class BoundingBox(left: Double, top: Double, right: Double, bottom: Double) {
  // TODO: Are bounding boxes every not symmetric around any given
  // axis? Can we replace l/t/r/b with just width and height?
  val height: Double =
    bottom - top

  val width: Double =
    right - left
}
