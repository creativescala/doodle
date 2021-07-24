package doodle
package image
package examples

import doodle.core._
import doodle.syntax._

// To use this example, open the SBT console and type:
//
// FunctionalGeometry.image.draw
//
// adapted From http://pmh-systems.co.uk/phAcademic/papers/funcgeo.pdf
object FunctionalGeometry {

  def grid(lines: List[((Int, Int), (Int, Int))]) = {
    import PathElement._
    val paths = lines.map { case (((a, b), (c, d))) =>
      Image.openPath(
        List(moveTo(a.toDouble, b.toDouble), lineTo(c.toDouble, d.toDouble))
      )
    }
    paths.foldLeft(Image.empty)(_ on _)
  }

  def quartet(a: Image, b: Image, c: Image, d: Image): Image =
    (a beside b) above (c beside d)

  def cycle(i: Image): Image =
    quartet(i, i rotate 270.degrees, i rotate 90.degrees, i rotate 180.degrees)

  def blank(m: Int = 4, n: Int = 4): Image =
    Image.rectangle(16, 16).scale(m.toDouble, n.toDouble).noStroke

  def nonet(
      i1: Image,
      i2: Image,
      i3: Image,
      i4: Image,
      i5: Image,
      i6: Image,
      i7: Image,
      i8: Image,
      i9: Image
  ) = {
    (i1 beside (i2 beside i3)) above ((i4 beside (i5 beside i6)) above (i7 beside (i8 beside i9)))
  }

  val p: Image =
    grid(
      List(
        ((4, 4), (6, 0)),
        ((0, 3), (3, 4)),
        ((3, 4), (0, 8)),
        ((0, 8), (0, 3)),
        ((4, 5), (7, 6)),
        ((7, 6), (4, 10)),
        ((4, 10), (4, 5)),
        ((11, 0), (10, 4)),
        ((10, 4), (8, 8)),
        ((8, 8), (4, 13)),
        ((4, 13), (0, 16)),
        ((11, 0), (14, 2)),
        ((14, 2), (16, 2)),
        ((10, 4), (13, 5)),
        ((13, 5), (16, 4)),
        ((9, 6), (12, 7)),
        ((12, 7), (16, 6)),
        ((8, 8), (12, 9)),
        ((12, 9), (16, 8)),
        ((8, 12), (16, 10)),
        ((0, 16), (6, 15)),
        ((6, 15), (8, 16)),
        ((8, 16), (12, 12)),
        ((12, 12), (16, 12)),
        ((10, 16), (12, 14)),
        ((12, 14), (16, 13)),
        ((12, 16), (13, 15)),
        ((13, 15), (16, 14)),
        ((14, 16), (16, 15))
      )
    )

  val q: Image =
    grid(
      List(
        ((2, 0), (4, 5)),
        ((4, 5), (4, 7)),
        ((4, 0), (6, 5)),
        ((6, 5), (6, 7)),
        ((6, 0), (8, 5)),
        ((8, 5), (8, 8)),
        ((8, 0), (10, 6)),
        ((10, 6), (10, 9)),
        ((10, 0), (14, 11)),
        ((12, 0), (13, 4)),
        ((13, 4), (16, 8)),
        ((16, 8), (15, 10)),
        ((15, 10), (16, 16)),
        ((16, 16), (12, 10)),
        ((12, 10), (6, 7)),
        ((6, 7), (4, 7)),
        ((4, 7), (0, 8)),
        ((13, 0), (16, 6)),
        ((14, 0), (16, 4)),
        ((15, 0), (16, 2)),
        ((0, 10), (7, 11)),
        ((9, 12), (10, 10)),
        ((10, 10), (12, 12)),
        ((12, 12), (9, 12)),
        ((8, 15), (9, 13)),
        ((9, 13), (11, 15)),
        ((11, 15), (8, 15)),
        ((0, 12), (3, 13)),
        ((3, 13), (7, 15)),
        ((7, 15), (8, 16)),
        ((2, 16), (3, 13)),
        ((4, 16), (5, 14)),
        ((6, 16), (7, 15))
      )
    )

  val r: Image =
    grid(
      List(
        ((0, 12), (1, 14)),
        ((0, 8), (2, 12)),
        ((0, 4), (5, 10)),
        ((0, 0), (8, 8)),
        ((1, 1), (4, 0)),
        ((2, 2), (8, 0)),
        ((3, 3), (8, 2)),
        ((8, 2), (12, 0)),
        ((5, 5), (12, 3)),
        ((12, 3), (16, 0)),
        ((0, 16), (2, 12)),
        ((2, 12), (8, 8)),
        ((8, 8), (14, 6)),
        ((14, 6), (16, 4)),
        ((6, 16), (11, 10)),
        ((11, 10), (16, 6)),
        ((11, 16), (12, 12)),
        ((12, 12), (16, 8)),
        ((12, 12), (16, 16)),
        ((13, 13), (16, 10)),
        ((14, 14), (16, 12)),
        ((15, 15), (16, 14))
      )
    )

  val s: Image =
    grid(
      List(
        ((0, 0), (4, 2)),
        ((4, 2), (8, 2)),
        ((8, 2), (16, 0)),
        ((0, 4), (2, 1)),
        ((0, 6), (7, 4)),
        ((0, 8), (8, 6)),
        ((0, 10), (7, 8)),
        ((0, 12), (7, 10)),
        ((0, 14), (7, 13)),
        ((8, 16), (7, 13)),
        ((7, 13), (7, 8)),
        ((7, 8), (8, 6)),
        ((8, 6), (10, 4)),
        ((10, 4), (16, 0)),
        ((10, 16), (11, 10)),
        ((10, 6), (12, 4)),
        ((12, 4), (12, 7)),
        ((12, 7), (10, 6)),
        ((13, 7), (15, 5)),
        ((15, 5), (15, 8)),
        ((15, 8), (13, 7)),
        ((12, 16), (13, 13)),
        ((13, 13), (15, 9)),
        ((15, 9), (16, 8)),
        ((13, 13), (16, 14)),
        ((14, 11), (16, 12)),
        ((15, 9), (16, 10))
      )
    )

  val t: Image = quartet(p, q, r, s)

  val u: Image = cycle(q rotate 90.degrees)

  val side1: Image =
    quartet(blank(), blank(), t.scale(2, 2).rotate(90.degrees), t.scale(2, 2))

  val side2: Image =
    quartet(side1, side1, (t rotate 90.degrees).scale(4, 4), t.scale(4, 4))

  val corner1: Image = quartet(blank(), blank(), blank(), u.scale(2, 2))

  val corner2: Image =
    quartet(corner1, side1, side1 rotate 90.degrees, u.scale(4, 4))

  val pseudocorner: Image =
    quartet(
      corner2,
      side2,
      side2 rotate 90.degrees,
      (t rotate 90.degrees).scale(8, 8)
    )

  val fishes: Image = cycle(pseudocorner)

  // the at call is needed because putting 2 different sized images beside
  // each other aligns at the top, but we need them aligned at the bottom
  val corner: Image = nonet(
    corner2,
    side2,
    side2,
    side2 rotate 90.degrees,
    u.scale(8, 8),
    (t rotate 90.degrees).scale(8, 8),
    side2 rotate 90.degrees,
    (t rotate 90.degrees).scale(8, 8),
    (q rotate 90.degrees).scale(16, 16).at(0, -128)
  )

  val squarelimit: Image = cycle(corner)

  val image: Image = squarelimit

}
