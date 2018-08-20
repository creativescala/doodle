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

import java.awt.Graphics2D
import doodle.engine.Engine

package object java2d {
  type Algebra = doodle.java2d.algebra.Algebra
  type Drawing[A] = doodle.algebra.generic.Finalized[Graphics2D,A]
  type Contextualized[A] = doodle.algebra.generic.Contextualized[Graphics2D,A]
  type Renderable[A] = doodle.algebra.generic.Renderable[A]

  type Java2DFrame = doodle.java2d.engine.Java2DFrame
  implicit val java2DEngine: Engine[Algebra, Drawing, Java2DFrame] =
    doodle.java2d.engine.Engine
  implicit val java2dGifWriter = doodle.java2d.engine.Java2dGifWriter
  implicit val java2dPngWriter = doodle.java2d.engine.Java2dPngWriter
  implicit val java2dJpgWriter = doodle.java2d.engine.Java2dJpgWriter

  type Image[A] = doodle.algebra.Image[Algebra,Drawing,A]
  object Image {
    def apply(f: Algebra => Drawing[Unit]): Image[Unit] = {
      new Image[Unit] {
        def apply(implicit algebra: Algebra): Drawing[Unit] =
          f(algebra)
      }
    }
  }
}
