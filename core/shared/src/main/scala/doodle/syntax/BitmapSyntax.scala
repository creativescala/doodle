/*
 * Copyright 2015-2020 Noel Welsh
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

import doodle.algebra.{Picture, Bitmap}
import java.io.File

trait BitmapSyntax {
  def read[Alg[x[_]] <: Bitmap[x], F[_]](file: File): Picture[Alg, F, Unit] =
    Picture{ implicit algebra: Alg[F] =>
      algebra.read(file)
    }

  def read[Alg[x[_]] <: Bitmap[x], F[_]](file: String): Picture[Alg, F, Unit] =
    Picture{ implicit algebra: Alg[F] =>
      algebra.read(file)
    }
}
