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
package animate
package examples

import doodle.algebra.Image
import doodle.core._
import doodle.language.Basic
import doodle.syntax._

object Orbit {
  def image[F[_]](angle: Angle): Image[Basic[F],F,Unit] =
    Basic.image[F,Unit]{ implicit algebra: Basic[F] =>
      import algebra._

      circle(10).at(angle.sin * 200, angle.cos * 200).fillColor(Color.cornSilk)
    }

  def frames[F[_]]: List[Image[Basic[F],F,Unit]] =
    List.range(0, 3600)
      .map{ angle => angle.degrees }
      .map{ angle => image[F](angle) }
}
