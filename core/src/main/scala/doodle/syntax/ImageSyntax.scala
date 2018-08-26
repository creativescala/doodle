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
package syntax

import doodle.engine.{Engine,Frame}
import doodle.image.Image
import doodle.language.Basic

trait ImageSyntax {
  implicit class ImageOps(image: Image) {
    def draw[Algebra[A[?]] <: Basic[A[?]],F[_],C](implicit engine: Engine[Algebra[F], F, C]): Unit =
    (for {
       canvas <- engine.frame(Frame.fitToImage())
       a      <- engine.render(canvas)(algebra => image.compile(algebra))
     } yield a).unsafeRunSync()
  }
}
