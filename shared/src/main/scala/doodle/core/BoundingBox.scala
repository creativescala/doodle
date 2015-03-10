package doodle.core

/**
  * Represents a bounding box around an image. The coordinate system
  * follows the canvas convention, meaning that the origin is the top
  * left. However, the coordinate system is not the global coordinate
  * system of the canvas but one that is local to the image. We
  * require the image is centered at the origin, and thus left and top
  * will usually be negative.
  */
final case class BoundingBox(left: Double, top: Double, right: Double, bottom: Double) {
  def center = Vec((left + right) / 2, (top + bottom) / 2)

  // TODO: Are bounding boxes ever not symmetric around any given
  // axis? Can we replace l/t/r/b with just width and height?
  val height: Double =
    bottom - top

  val width: Double =
    right - left

  def expand(toInclude: Vec): BoundingBox =
    BoundingBox(
      left   min toInclude.x,
      top    min toInclude.y,
      right  max toInclude.x,
      bottom max toInclude.y
    )

  def translate(v: Vec): BoundingBox =
    BoundingBox(left + v.x, top + v.y, right + v.x, bottom + v.y)
}

object BoundingBox {
  val empty: BoundingBox = BoundingBox(0.0, 0.0, 0.0, 0.0)

  def apply(vec: Vec): BoundingBox =
    BoundingBox(vec.x, vec.y, vec.x, vec.y)

  def apply(vecs: Seq[Vec]): BoundingBox = vecs match {
    case Seq()    => BoundingBox.empty
    case Seq(hd)  => BoundingBox(hd)
    case hd +: tl => tl.foldLeft(BoundingBox(hd))(_ expand _)
  }

  def apply(image: Image): BoundingBox = image match {
    case Path(elts) =>
      BoundingBox(elts.flatMap {
        case MoveTo(pos) => Seq(pos)
        case LineTo(pos) => Seq(pos)
        case BezierCurveTo(cp1, cp2, pos) =>
          // The control points form a bounding box around a bezier curve,
          // but this may not be a tight bounding box.
          // It's an acceptable solution for now but in the future
          // we may wish to generate a tighted bounding box.
          Seq(cp1, cp2, pos)
      })

    case Circle(r) =>
      BoundingBox(-r, -r, r, r)

    case Rectangle(w, h) =>
      BoundingBox(-w/2, -h/2, w/2, h/2)

    case Triangle(w, h) =>
      BoundingBox(-w/2, -h/2, w/2, h/2)

    case Overlay(t, b) =>
      val BoundingBox(l1, t1, r1, b1) = BoundingBox(t)
      val BoundingBox(l2, t2, r2, b2) = BoundingBox(b)
      BoundingBox(l1 min l2, t1 min t2, r1 max r2, b1 max b2)

    case Beside(l, r) =>
      val boxL = BoundingBox(l)
      val boxR = BoundingBox(r)
      BoundingBox(
        -(boxL.width + boxR.width) / 2,
        boxL.top min boxR.top,
        (boxL.width + boxR.width) / 2,
        boxL.bottom max boxR.bottom
      )

    case Above(t, b) =>
      val boxT = BoundingBox(t)
      val boxB = BoundingBox(b)

      BoundingBox(
        boxT.left min boxB.left,
        -(boxT.height + boxB.height) / 2,
        boxT.right max boxB.right,
        (boxT.height + boxB.height) / 2
      )

    case At(v, i) =>
      BoundingBox(i) translate v

    case ContextTransform(f, i) =>
      BoundingBox(i)

    case d: Drawable =>
      BoundingBox(d.draw)
  }
}
