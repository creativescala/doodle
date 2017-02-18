package doodle

import org.scalacheck.{Arbitrary, Gen}

object arbitrary {
  import doodle.core._
  import doodle.syntax._

  final case class Translate(x: Double, y: Double)
  final case class Scale(x: Double, y: Double)
  final case class Rotate(angle: Angle)

  implicit val arbitraryPoint: Arbitrary[Point] = Arbitrary(
    for {
      x <- Gen.choose(-1000.0, 1000.0)
      y <- Gen.choose(-1000.0, 1000.0)
    } yield Point.cartesian(x, y)
  )

  implicit val arbitraryAngle: Arbitrary[Angle] = Arbitrary(
    Gen.choose(-36.0, 36.0).map { angle => Angle(angle) }
  )

  implicit val arbitraryScale: Arbitrary[Scale] = Arbitrary(
    Arbitrary.arbitrary[Point].map { pt => Scale(pt.x, pt.y) }
  )

  implicit val arbitraryRotate: Arbitrary[Rotate] = Arbitrary(
    Gen.choose(-720.0, 720.0).map { d => Rotate(d.degrees) }
  )

  implicit val arbitraryTranslate: Arbitrary[Translate] = Arbitrary(
    Arbitrary.arbitrary[Point].map { pt => Translate(pt.x, pt.y) }
  )
}
