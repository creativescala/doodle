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

import doodle.algebra.generic.reified.Reification
import doodle.effect.DefaultRenderer
import doodle.language.Basic

package object java2d {
  type Algebra[F[_]] = doodle.algebra.Algebra[F] with Basic[F]
  type Drawing[A] = doodle.algebra.generic.Finalized[Reification,A]
  type Renderable[A] = doodle.algebra.generic.Renderable[Reification,A]

  type Java2DFrame = doodle.java2d.effect.Java2DFrame
  implicit val java2DRenderer: DefaultRenderer[Algebra, Drawing, doodle.java2d.effect.Frame, Java2DFrame] =
    doodle.java2d.effect.Java2dRenderer
  implicit val java2dGifWriter = doodle.java2d.effect.Java2dGifWriter
  implicit val java2dPngWriter = doodle.java2d.effect.Java2dPngWriter
  implicit val java2dJpgWriter = doodle.java2d.effect.Java2dJpgWriter

  type Image[A] = doodle.algebra.Image[Algebra, Drawing, A]
  object Image {
    def apply(f: Algebra[Drawing] => Drawing[Unit]): Image[Unit] = {
      new Image[Unit] {
        def apply(implicit algebra: Algebra[Drawing]): Drawing[Unit] =
          f(algebra)
      }
    }
  }
}
