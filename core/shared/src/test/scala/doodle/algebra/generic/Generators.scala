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

package doodle.algebra.generic

import cats.instances.unit.*
import doodle.algebra.generic.*
import doodle.algebra.generic.reified.Reification
import doodle.algebra.generic.reified.Reified
import doodle.core.Transform as Tx
import org.scalacheck.*

trait Generators extends doodle.core.Generators {

  val width: Gen[Double] = Gen.posNum[Double]
  val height = width

  def sizeToDepth(size: Int): Int =
    (Math.log(size.toDouble) / Math.log(2.0)).toInt

  /*
  def blend(algebra: TestAlgebra, depth: Int): Gen[Finalized[Unit]] = {
    val child = finalizedOfDepth(algebra, depth - 1)
    for {
      one  <- child
      node <- Gen.oneOf(
        algebra.screen(one),
        algebra.burn(one),
        algebra.dodge(one),
        algebra.lighten(one),
        algebra.sourceOver(one)
      )
    } yield node
  }
   */

  def layout(
      algebra: TestAlgebra,
      depth: Int
  ): Gen[Finalized[Reification, Unit]] = {
    val child = finalizedOfDepth(algebra, depth - 1)
    for {
      one <- child
      two <- child
      node <- Gen.oneOf(
        algebra.on(one, two),
        algebra.beside(one, two),
        algebra.above(one, two),
        algebra.under(one, two),
        algebra.below(one, two)
      )
    } yield node
  }

  def shape(algebra: TestAlgebra): Gen[Finalized[Reification, Unit]] =
    Gen.oneOf(
      Gen.zip(width, height).map { case (w, h) => algebra.rectangle(w, h) },
      width.map(w => algebra.square(w)),
      Gen.zip(width, height).map { case (w, h) => algebra.triangle(w, h) },
      width.map(w => algebra.circle(w)),
      Gen.const(algebra.empty)
    )

  def style(
      algebra: TestAlgebra,
      depth: Int
  ): Gen[Finalized[Reification, Unit]] = {
    val child = finalizedOfDepth(algebra, depth - 1)
    for {
      one <- child
      node <- Gen.oneOf(
        color.map(c => algebra.fillColor(one, c)),
        color.map(c => algebra.strokeColor(one, c)),
        Gen.choose(1.0, 20.0).map(w => algebra.strokeWidth(one, w)),
        Gen.const(algebra.noFill(one)),
        Gen.const(algebra.noStroke(one))
      )
    } yield node
  }

  def finalizedOfDepth(
      algebra: TestAlgebra,
      depth: Int
  ): Gen[Finalized[Reification, Unit]] =
    if depth <= 0 then shape(algebra)
    else
      Gen.oneOf(
        /*blend(algebra, depth),*/ layout(algebra, depth),
        shape(algebra),
        style(algebra, depth)
      )

  val finalized: Gen[Finalized[Reification, Unit]] =
    Gen.sized { size =>
      val depth = sizeToDepth(size)
      finalizedOfDepth(TestAlgebra(), depth)
    }

  def reify[A](finalized: Finalized[Reification, A]): List[Reified] = {
    val (_, rdr) = finalized.run(List.empty).value
    val (_, fa) = rdr.run(Tx.identity).value
    val (reified, _) = fa.run.value
    reified
  }
}
object Generators extends Generators
