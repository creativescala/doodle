/*
 * Copyright 2015 noelwelsh
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
import doodle.core.{Transform => Tx}
import org.scalacheck._
import org.scalacheck.Prop._

object LayoutSpec extends Properties("Layout properties") {
  val style = TestAlgebra()

  property("above doubles size of image") =
    forAllNoShrink(Generators.width){ width =>
      implicit val algebra = TestAlgebra()
      val square = algebra.square(width)
      val circle = algebra.circle(width)
      val triangle = algebra.triangle(width,width)

      val examples =
        for {
          i <- List(square, circle, triangle)
          j <- List(square, circle, triangle)
        } yield {
          val img = algebra.noStroke(algebra.above(i,j))
          val (bb, rdr) = img.runA(List.empty).value
          val (_, fa) = rdr.run(Tx.identity).value
          val (reified, _) = fa.run.value

          (bb.height ?= (2 * width)) :| s"Height is ${bb.height} with width $width for image that reifies to $reified"
          (bb.width ?= width) :| s"Width is ${bb.width} when it should be $width for image that reified to $reified"
        }

      all(examples: _*)
    }

  property("above reifies correctly") =
    forAllNoShrink(Generators.width){ width =>
      import doodle.algebra.generic.reified.Reified._
      import doodle.core.Transform

      implicit val algebra = TestAlgebra()
      val square = algebra.square(width)
      val img = algebra.above(square, square)
      val (_, rdr) = img.runA(List.empty).value
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
          ((tx1 ?= Tx.identity.andThen(Transform.translate(0, (width + 1.0) / 2.0))) :| "Top transform") &&
          ((tx2 ?= Tx.identity.andThen(Transform.translate(0, -(width + 1.0) / 2.0))) :| "Bottom transform")
      }
    }
}
