package doodle
package core

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

  property("horizontalReflection") =
    forAll{ (point: Point) =>
      Transform.horizontalReflection(point) ?= Point(-point.x, point.y)
    }

  property("verticalReflection") =
    forAll{ (point: Point) =>
      Transform.verticalReflection(point) ?= Point(point.x, -point.y)
    }

  property("logicalToScreen") =
    forAll{ (screen: Screen, point: Point) =>
      val expected = Point(point.x + (screen.width.toDouble / 2.0), (screen.height.toDouble / 2.0) - point.y)
      val tx = Transform.logicalToScreen(screen.width.toDouble, screen.height.toDouble)
      val actual = tx(point)

      (actual ~= expected) :| s"actual: $actual\nexpected: $expected"
    }

  property("screenToLogical") =
    forAll{ (screen: Screen, point: Point) =>
      val expected = Point(point.x - (screen.width.toDouble  / 2.0), (screen.height.toDouble / 2.0) - point.y)
      val tx = Transform.screenToLogical(screen.width.toDouble, screen.height.toDouble)
      val actual = tx(point)

      (actual ~= expected) :| s"actual: $actual\nexpected: $expected"
    }
}
