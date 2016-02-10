package doodle
package backend

/**
  * This Image representation serves as the intermediate form used for
  * compilation of `core.Image` into a form that can rendered. It's main use is
  * to associate a `DrawingContext` with every path in the graph. This allows us
  * to calculate the true size of the bounding box taking into account the line
  * width.
  */
sealed trait Image {
  def boundingBox: BoundingBox
}
final case class Path(context: DrawingContext, elements: Seq[PathElement]) extends Image
final case class Beside(l: Image, r: Image) extends Image
final case class Above(l: Image, r: Image) extends Image
final case class On(t: Image, b: Image) extends Image
final case class At(at: Vec, i: Image) extends Image
final case object Empty extends Image 
