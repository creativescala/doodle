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
package effect

import cats.effect.IO
import doodle.algebra.Algebra
import doodle.algebra.Picture

import java.awt.image.BufferedImage

/** The BufferedImageWriter type represent the ability to encode an image as a
  * java BufferedImage class.
  */
trait BufferedImageWriter[+Alg <: Algebra, Frame] extends Writer[Alg, Frame] {
  def bufferedImage[A](
      description: Frame,
      picture: Picture[Alg, A]
  ): IO[(A, BufferedImage)]

  def bufferedImage[A](picture: Picture[Alg, A])(using
      frame: DefaultFrame[Frame]
  ): IO[(A, BufferedImage)] =
    bufferedImage(frame.default, picture)
}
