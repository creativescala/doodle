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
package java2d
package effect

import cats.effect.IO
import doodle.effect._
import doodle.java2d.effect.{Java2d => Java2dEffect}

import java.awt.image.BufferedImage

object Java2dBufferedImageWriter
    extends BufferedImageWriter[doodle.java2d.Algebra, Frame] {
  def makeImage(width: Int, height: Int): BufferedImage =
    new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

  def bufferedImage[A](
      frame: Frame,
      picture: Picture[A]
  ): IO[(A, BufferedImage)] = for {
    result <- Java2dEffect.renderBufferedImage(
      frame.size,
      frame.center,
      frame.background,
      picture
    )(makeImage _)
    (bi, a) = result
  } yield (a, bi)
}
