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
package svg
package algebra

import cats._
import doodle.algebra.generic.Finalized
import doodle.algebra.generic.Renderable
import doodle.core.BoundingBox
import doodle.core.font.Font

trait TestAlgebraModule
    extends AlgebraModule
    with PathModule
    with ShapeModule
    with SvgModule
    with TestBase {

  type Algebra = TestAlgebra

  final class TestAlgebra(
      val applyF: Apply[SvgResult],
      val functorF: Functor[SvgResult]
  ) extends BaseAlgebra {
    def font[A](image: Drawing[A], font: Font): TestAlgebra.this.Drawing[A] =
      ???
    def text(text: String): Drawing[Unit] = ???

    implicit val drawingInstance: cats.Monad[Drawing] =
      new Monad[Drawing] {
        def pure[A](x: A): Drawing[A] =
          Finalized.leaf(_ =>
            (
              BoundingBox.empty,
              Renderable(_ => Eval.now(Svg.svgResultApplicative.pure(x)))
            )
          )

        def flatMap[A, B](fa: Drawing[A])(f: A => Drawing[B]): Drawing[B] =
          fa.flatMap { (bb, rdr) =>
            val (_, _, a) = rdr.runA(doodle.core.Transform.identity).value
            f(a)
          }

        def tailRecM[A, B](a: A)(f: A => Drawing[Either[A, B]]): Drawing[B] = {
          // TODO: This implementation is not tail recursive but I don't think we need it for what we use in Doodle
          val dAB = f(a)
          flatMap(dAB)(either =>
            either match {
              case Left(a)  => tailRecM(a)(f)
              case Right(b) => dAB.asInstanceOf[Drawing[B]]
            }
          )
        }
      }

    // Members declared in doodle.algebra.generic.GivenApply
    implicit val applyDrawing: cats.Apply[SvgResult] =
      Svg.svgResultApplicative

    // Members declared in doodle.algebra.generic.GivenFunctor
    implicit val functorDrawing: cats.Functor[SvgResult] =
      Svg.svgResultApplicative
  }
  val algebraInstance =
    new TestAlgebra(Svg.svgResultApplicative, Svg.svgResultApplicative)
}
object TestAlgebra extends TestAlgebraModule {}
