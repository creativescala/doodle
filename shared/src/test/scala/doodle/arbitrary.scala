package doodle

import org.scalacheck.{Arbitrary, Gen}

object arbitrary {
  import doodle.core._

  val genAngle: Gen[Angle] =
    Gen.choose(-36.0, 36.0).map { angle => Angle(angle) }

  val genPoint: Gen[Point] =
    for {
      x <- Gen.choose(-1000.0, 1000.0)
      y <- Gen.choose(-1000.0, 1000.0)
    } yield Point.cartesian(x, y)

  val genNormalized: Gen[Normalized] =
    Gen.choose(0.0, 1.0) map Normalized.clip

  val genUnsignedByte: Gen[UnsignedByte] =
    Gen.choose(0, 255) map (UnsignedByte.clip _)

  val genHSLA: Gen[HSLA] =
    for {
      h <- genAngle
      s <- genNormalized
      l <- genNormalized
      a <- genNormalized
    } yield HSLA(h.normalize, s, l, a)

  val genRGBA: Gen[RGBA] =
    for {
      r <- genUnsignedByte
      g <- genUnsignedByte
      b <- genUnsignedByte
      a <- genNormalized
    } yield RGBA(r, g, b, a)

  final case class Translate(x: Double, y: Double)
  final case class Scale(x: Double, y: Double)
  final case class Rotate(angle: Angle)

  implicit val arbitraryPoint: Arbitrary[Point] = Arbitrary(
    genPoint
  )

  implicit val arbitraryAngle: Arbitrary[Angle] = Arbitrary(
    genAngle
  )

  implicit val arbitraryScale: Arbitrary[Scale] = Arbitrary(
    genPoint.map { pt => Scale(pt.x, pt.y) }
  )

  implicit val arbitraryRotate: Arbitrary[Rotate] = Arbitrary(
    genAngle.map { d => Rotate(d) }
  )

  implicit val arbitraryTranslate: Arbitrary[Translate] = Arbitrary(
    genPoint.map { pt => Translate(pt.x, pt.y) }
  )

  implicit val arbitraryHSLA: Arbitrary[HSLA] = Arbitrary(
    genHSLA
  )

  implicit val arbitraryRGBA: Arbitrary[RGBA] = Arbitrary(
    genRGBA
  )
}
