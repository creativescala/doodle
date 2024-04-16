/*
 * Copyright 2015 Creative Scala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package doodle
package algebra
package generic

import cats.implicits._
import doodle.algebra.generic.reified.Reification
import doodle.core.BoundingBox
import doodle.core.{Transform => Tx}
import org.scalacheck.Prop._
import org.scalacheck._

object LayoutSpec extends Properties("Layout properties") {
  val style = TestAlgebra()

  // Height of a hexagon with radius r with the widest part of the hexagon aligned with the x axis.
  def hexagonHeight(r: Double): Double =
    (Math.sqrt(3.0) * r) / 2.0

  property("hand generated path bounding boxes are correct") = {
    import doodle.core._
    import doodle.syntax.approximatelyEqual._
    import Instances._

    implicit val algebra = TestAlgebra()
    val verticalLine =
      algebra.path(
        OpenPath(
          List(
            PathElement.moveTo(0, -100),
            PathElement.lineTo(0, 100)
          )
        )
      )
    val horizontalLine =
      algebra.path(
        OpenPath(
          List(
            PathElement.moveTo(-100, 0),
            PathElement.lineTo(100, 0)
          )
        )
      )
    val hexagon =
      algebra.noStroke(algebra.regularPolygon(6, 100))
    val hexagonH = hexagonHeight(100)

    (verticalLine.boundingBox ?= BoundingBox(-1, 101, 1, -101)) &&
    (horizontalLine.boundingBox ?= BoundingBox(-101, 1, 101, -1)) &&
    (hexagon.boundingBox ~= BoundingBox(-100, hexagonH, 100, -hexagonH))
  }

  property("hand generated at bounding boxes are correct") = {
    import doodle.syntax.all._
    import doodle.syntax.approximatelyEqual._
    import doodle.algebra.generic._
    import Instances._
    import TestAlgebra._

    implicit val algebra = TestAlgebra()
    val hexagon =
      regularPolygon[Algebra](6, 100).noStroke

    val hexhex =
      List(
        hexagon.at(100, 0.degrees),
        hexagon.at(100, 60.degrees),
        hexagon.at(100, 120.degrees),
        hexagon.at(100, 180.degrees),
        hexagon.at(100, 240.degrees),
        hexagon.at(100, 300.degrees)
      ).allOn

    val height = hexagonHeight(200)

    val actual = (hexhex(algebra): Finalized[Reification, Unit]).boundingBox
    val expected = BoundingBox(-200, height, 200, -height)

    (actual ~= expected) :| s"Actual bounding box $actual while expected $expected"
  }

  property("at never decreases the size of the bounding box") =
    forAllNoShrink(Generators.width, Generators.height) { (x, y) =>
      implicit val algebra = TestAlgebra()
      val hexagon: Finalized[Reification, Unit] = algebra.regularPolygon(6, 100)
      val initialBb = hexagon.boundingBox
      val grow: Finalized[Reification, Unit] = algebra.at(hexagon, x, y)
      val atBb = grow.boundingBox

      val atSize = (atBb.width * atBb.height)
      val initialSize = (initialBb.width * initialBb.height)

      // Allow for a little bit of rounding / FP error
      (atSize - initialSize >= -0.01) :| s"Bounding box $atBb with size $atSize and displacement ($x, $y), was smaller than $initialBb with size $initialSize"
    }

  property("above doubles size of image") = forAllNoShrink(Generators.width) {
    width =>
      implicit val algebra = TestAlgebra()
      val square = algebra.square(width)
      val circle = algebra.circle(width)
      val triangle = algebra.triangle(width, width)

      val examples =
        for {
          i <- List(square, circle, triangle)
          j <- List(square, circle, triangle)
        } yield {
          val img = algebra.noStroke(algebra.above(i, j))
          val (bb, rdr) = img.run(List.empty).value
          val (_, fa) = rdr.run(Tx.identity).value
          val (reified, _) = fa.run.value

          (bb.height ?= (2 * width)) :| s"Height is ${bb.height} with width $width for image that reifies to $reified"
          (bb.width ?= width) :| s"Width is ${bb.width} when it should be $width for image that reified to $reified"
        }

      all(examples: _*)
  }

  property("above reifies correctly") = forAllNoShrink(Generators.width) {
    width =>
      import doodle.algebra.generic.reified.Reified._
      import doodle.core.Transform

      implicit val algebra = TestAlgebra()
      val square = algebra.square(width)
      val img = algebra.above(square, square)
      val (_, rdr) = img.run(List.empty).value
      val (_, fa) = rdr.run(Tx.identity).value
      val (reified, _) = fa.run.value

      (reified.size ?= 2) :| s"Expected two rendered elements but there were ${reified.size} in $reified"
      reified match {
        case List(StrokeRect(tx1, _, w1, h1), StrokeRect(tx2, _, w2, h2)) =>
          ((w1 ?= width) :| "Top width") &&
            ((h1 ?= width) :| "Top height") &&
            ((w2 ?= width) :| "Bottom width") &&
            ((h2 ?= width) :| "Bottom height") &&
            // The translation must account for the width of the line (1.0) in
            // addition to the width of the shape.
            ((tx1 ?= Tx.identity.andThen(
              Transform.translate(0, (width + 1.0) / 2.0)
            )) :| "Top transform") &&
            ((tx2 ?= Tx.identity.andThen(
              Transform.translate(0, -(width + 1.0) / 2.0)
            )) :| "Bottom transform")

        case other =>
          falsified :| s"Reached a case with value ${other} which should not have happened"
      }
  }

  property(
    "above generates bounding boxes with the correct size"
  ) = {
    val algebra = TestAlgebra()
    val genShape = Generators.finalizedOfDepth(algebra, 5)

    forAllNoShrink(genShape, genShape) { (shape1, shape2) =>
      val above = algebra.above(shape1, shape2)
      val bb = above.boundingBox
      val s1Bb = shape1.boundingBox
      val s2Bb = shape2.boundingBox

      val maxWidth = s1Bb.width.max(s2Bb.width)

      (bb ?= s1Bb.above(s2Bb)) &&
      (bb.height ?= (s1Bb.height + s2Bb.height)) &&
      (bb.width ?= maxWidth)
    }
  }

  property(
    "beside generates bounding boxes with the correct size"
  ) = {
    val algebra = TestAlgebra()
    val genShape = Generators.finalizedOfDepth(algebra, 5)

    forAllNoShrink(genShape, genShape) { (shape1, shape2) =>
      val beside = algebra.beside(shape1, shape2)
      val bb = beside.boundingBox
      val s1Bb = shape1.boundingBox
      val s2Bb = shape2.boundingBox

      val maxHeight = s1Bb.height.max(s2Bb.height)

      (bb ?= s1Bb.beside(s2Bb)) &&
      (bb.width ?= (s1Bb.width + s2Bb.width)) &&
      (bb.height ?= maxHeight)
    }
  }

  property(
    "on generates bounding boxes with the correct size"
  ) = {
    val algebra = TestAlgebra()
    val genShape = Generators.finalizedOfDepth(algebra, 5)

    forAllNoShrink(genShape, genShape) { (shape1, shape2) =>
      val on = algebra.on(shape1, shape2)
      val bb = on.boundingBox
      val s1Bb = shape1.boundingBox
      val s2Bb = shape2.boundingBox

      val maxWidth = s1Bb.width.max(s2Bb.width)
      val maxHeight = s1Bb.height.max(s2Bb.height)

      (bb ?= s1Bb.on(s2Bb)) &&
      (bb.width ?= maxWidth) &&
      (bb.height ?= maxHeight)
    }
  }

  property("margin expands bounding box by the correct amount") = {
    val algebra = TestAlgebra()
    val genShape = Generators.finalizedOfDepth(algebra, 5)
    val genMargin = Gen.choose[Double](-50.0, 50.0)

    forAllNoShrink(genShape, genMargin, genMargin, genMargin, genMargin) {
      (shape, top, right, bottom, left) =>
        val bb = shape.boundingBox
        val newBb = algebra
          .margin(shape, top, right, bottom, left)
          .boundingBox

        (newBb.left ?= bb.left - left) &&
        (newBb.top ?= bb.top + top) &&
        (newBb.right ?= bb.right + right) &&
        (newBb.bottom ?= bb.bottom - bottom)
    }
  }

  property("size sets bounding box to the correct size") = {
    val algebra = TestAlgebra()
    val genShape = Generators.finalizedOfDepth(algebra, 5)
    val genDim = Gen.choose[Double](0.0, 50.0)

    forAllNoShrink(genShape, genDim, genDim) { (shape, width, height) =>
      val newBb = algebra
        .size(shape, width, height)
        .boundingBox

      (newBb.left ?= -(width / 2)) &&
      (newBb.right ?= (width / 2)) &&
      (newBb.top ?= (height / 2)) &&
      (newBb.bottom ?= -(height / 2))
    }
  }
}
