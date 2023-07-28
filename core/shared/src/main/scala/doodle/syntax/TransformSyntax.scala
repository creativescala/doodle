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
package syntax

import doodle.algebra.Algebra
import doodle.algebra.Picture
import doodle.algebra.Transform
import doodle.core.Angle
import doodle.core.Vec
import doodle.core.{Transform => Tx}

trait TransformSyntax {
  implicit class TransformPictureOps[Alg <: Algebra, A](
      picture: Picture[Alg, A]
  ) {

    def transform(tx: Tx): Picture[Alg with Transform, A] =
      new Picture[Alg with Transform, A] {
        def apply(implicit algebra: Alg with Transform): algebra.Drawing[A] =
          algebra.transform(picture(algebra), tx)
      }

    def scale(x: Double, y: Double): Picture[Alg with Transform, A] =
      new Picture[Alg with Transform, A] {
        def apply(implicit algebra: Alg with Transform): algebra.Drawing[A] =
          algebra.scale(picture(algebra), x, y)
      }

    def rotate(angle: Angle): Picture[Alg with Transform, A] =
      new Picture[Alg with Transform, A] {
        def apply(implicit algebra: Alg with Transform): algebra.Drawing[A] =
          algebra.rotate(picture(algebra), angle)
      }

    def translate(x: Double, y: Double): Picture[Alg with Transform, A] =
      new Picture[Alg with Transform, A] {
        def apply(implicit algebra: Alg with Transform): algebra.Drawing[A] =
          algebra.translate(picture(algebra), x, y)
      }

    def translate(vec: Vec): Picture[Alg with Transform, A] =
      new Picture[Alg with Transform, A] {
        def apply(implicit algebra: Alg with Transform): algebra.Drawing[A] =
          algebra.translate(picture(algebra), vec)
      }

    def horizontalReflection: Picture[Alg with Transform, A] =
      new Picture[Alg with Transform, A] {
        def apply(implicit algebra: Alg with Transform): algebra.Drawing[A] =
          algebra.horizontalReflection(picture(algebra))
      }
    def verticalReflection: Picture[Alg with Transform, A] =
      new Picture[Alg with Transform, A] {
        def apply(implicit algebra: Alg with Transform): algebra.Drawing[A] =
          algebra.verticalReflection(picture(algebra))
      }
  }
}
