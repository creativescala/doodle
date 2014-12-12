package doodle

sealed trait Image {
  val boundingBox: BoundingBox =
    this match {
      case Circle(r) =>
        BoundingBox(-r, -r, r, r)

      case Rectangle(w, h) =>
        BoundingBox(-w/2, -h/2, w/2, h/2)
        
      case Overlay(t, b) =>
        val BoundingBox(l1, t1, r1, b1) = t.boundingBox
        val BoundingBox(l2, t2, r2, b2) = b.boundingBox
        BoundingBox(l1 min l2, t1 min t2, r1 max r1, b1 max b2)

      case Beside(l, r) =>
        val boxL = l.boundingBox
        val boxR = r.boundingBox
        BoundingBox(
          - (boxL.width + boxR.width) / 2,
          boxL.top min boxR.top,
          (boxL.width + boxR.width) / 2,
          boxL.bottom max boxR.bottom
        )

      case Above(t, b) =>
        val boxT = t.boundingBox
        val boxB = b.boundingBox

        BoundingBox(
          boxT.left min boxB.left,
          -(boxT.height + boxB.height) / 2,
          boxT.right max boxB.right,
          (boxT.height + boxB.height) / 2
        )

      case StrokeColour(c, i) =>
        i.boundingBox
    }

  def beside(right: Image): Image =
    Beside(this, right)

  def on(bottom: Image): Image =
    Overlay(this, bottom)

  def under(top: Image): Image =
    Overlay(top, this)

  def above(bottom: Image): Image =
    Above(this, bottom)

  def below(top: Image): Image =
    Above(top, this)

  def strokeColour(colour: Colour): Image =
    StrokeColour(colour, this)
}
final case class Circle(r: Double) extends Image
final case class Rectangle(w: Double, h: Double) extends Image
final case class Beside(l: Image, r: Image) extends Image
final case class Above(l: Image, r: Image) extends Image
final case class Overlay(t: Image, b: Image) extends Image
// final case class Path(elements: List[Path]) extends Image {
//   def +:(elt: PathElement): Path =
//     Path(elt +: elements)

//   def :+(elt: PathElement): Path =
//     Path(elements :+ elt)

//   def ++(path: Path): Path =
//     Path(elements ++ path.elements)
// }
final case class StrokeColour(colour: Colour, image: Image) extends Image

