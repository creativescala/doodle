package doodle
package core
package transform

import org.scalatest._
import org.scalatest.prop.Checkers

class TransformSpec extends FlatSpec with Matchers with Checkers {
  import doodle.arbitrary._
  import doodle.syntax.approximatelyEqual._

  "scale" should "scale the x and y coordinates appropriately" in {
    check { (scale: Scale, point: Point) =>
      val expected = Point.cartesian(point.x * scale.x, point.y * scale.y)
      Transform.scale(scale.x, scale.y)(point) ~= expected
    }
  }

  "rotate" should "rotate the point" in {
    check{ (rotate: Rotate, point: Point) =>
      val expected = point.rotate(rotate.angle)
      Transform.rotate(rotate.angle)(point) ~= expected
    }
  }

  "andThen" should "compose the transformations" in {
    check{ (translate: Translate, rotate: Rotate, point: Point) =>
      val expected =
        Point.cartesian(point.x + translate.x, point.y + translate.y).rotate(rotate.angle)
      val tx =
        Transform.translate(translate.x, translate.y).andThen(Transform.rotate(rotate.angle))

      tx(point) ~= expected
    }
  }
}
