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

package doodle.syntax

import cats.effect.IO
import doodle.algebra.Algebra
import doodle.algebra.LoadBitmap
import doodle.algebra.Picture
import doodle.algebra.ToPicture

trait LoadBitmapSyntax {
  extension [S](specifier: S) {

    /** Load a bitmap from this specifier */
    def loadBitmap[B](using loader: LoadBitmap[S, B]): IO[B] =
      loader.load(specifier)

    /** Load a bitmap and immediately convert to Picture. This is a convenience
      * method that combines loading and conversion.
      */
    def loadAsPicture[B, Alg <: Algebra](using
        loader: LoadBitmap[S, B],
        tp: ToPicture[B, Alg]
    ): IO[Picture[Alg, Unit]] =
      loader.load(specifier).map(tp.toPicture)
  }

  extension [B](ioBitmap: IO[B]) {

    /** Transform the bitmap inside the IO */
    def mapBitmap[B2](f: B => B2): IO[B2] = ioBitmap.map(f)

    /** Convert to Picture inside the IO */
    def toPicture[Alg <: Algebra](using
        tp: ToPicture[B, Alg]
    ): IO[Picture[Alg, Unit]] =
      ioBitmap.map(tp.toPicture)
  }
}
