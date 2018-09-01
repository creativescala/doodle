package doodle
package core
package transform

import org.scalacheck._
import org.scalacheck.Prop._

class TransformSpec extends Properties("Transform") {
  import doodle.arbitrary._
  import doodle.syntax.approximatelyEqual._

  property("scale scale the x and y coordinates appropriately") =
    forAll{ (scale: Scale, point: Point) =>
      val expected = Point.cartesian(point.x * scale.x, point.y * scale.y)
      Transform.scale(scale.x, scale.y)(point) ~= expected
    }

  property("rotate rotate the point") =
    forAll{ (rotate: Rotate, point: Point) =>
      val expected = point.rotate(rotate.angle)
      Transform.rotate(rotate.angle)(point) ~= expected
    }

  property("andThen compose the transformations") =
    forAll{ (translate: Translate, rotate: Rotate, point: Point) =>
      val expected =
        Point.cartesian(point.x + translate.x, point.y + translate.y).rotate(rotate.angle)
      val tx =
        Transform.translate(translate.x, translate.y).andThen(Transform.rotate(rotate.angle))

      tx(point) ~= expected
    }
}
