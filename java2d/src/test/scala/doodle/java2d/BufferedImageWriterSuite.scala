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

package doodle.java2d

import cats.effect.unsafe.implicits.global
import doodle.core.format.*
import doodle.syntax.all.*
import munit.CatsEffectSuite

import java.io.File
import javax.imageio.ImageIO

class BufferedImageWriteSuite extends CatsEffectSuite {
  val picture = Picture.circle(20.0)

  test(
    "writing to file produces the same output as writing BufferedImage to file"
  ) {
    val file = new File("buffered-image.png")

    for {
      _ <- picture.writeToIO[Png](file)
      _ = assert(file.exists())
      r <- picture.bufferedImageToIO()
      (_, bi) = r
      bi2 = ImageIO.read(file)
    } yield {
      assertEquals(bi.getWidth(), bi2.getWidth())
      assertEquals(bi.getHeight(), bi2.getHeight())

      assert(file.delete())
    }
  }
}
