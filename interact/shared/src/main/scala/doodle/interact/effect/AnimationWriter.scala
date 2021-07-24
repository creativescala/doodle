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
package interact
package effect

import cats.Monoid
import cats.effect.IO
import doodle.algebra.Algebra
import doodle.algebra.Picture
import monix.execution.Scheduler
import monix.reactive.Observable

import java.io.File

/** The `AnimationWriter` typeclass describes a data type that can write an
  * animation to a file.
  */
trait AnimationWriter[Alg[x[_]] <: Algebra[x], F[_], Frame, Format] {

  def write[A](
      file: File,
      description: Frame,
      frames: Observable[Picture[Alg, F, A]]
  )(implicit s: Scheduler, m: Monoid[A]): IO[A]
}
