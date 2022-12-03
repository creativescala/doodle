/*
 * Copyright 2015 Noel Welsh
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
package golden

import cats.effect.unsafe.implicits.global
import doodle.core.format._
import doodle.java2d._
import munit._

trait GoldenImage extends Golden { self: FunSuite =>
  import doodle.image._
  import doodle.image.syntax.all._

  def assertGoldenImage(name: String, image: Image)(implicit loc: Location) = {
    import java.io.File
    val file = new File(s"${goldenDir}/${name}.png")

    if (file.exists()) {
      val temp = new File(s"${goldenDir}/${name}.tmp.png")

      try {
        // We must do these operations sequentially. If we used the `write`
        // syntax instead of the `writeToIO` syntax the writing occurs
        // asynchronously as may not finish before we attempt to calculate the
        // image diff.
        image
          .writeToIO[Png](temp)
          .map(_ => imageDiff(file, temp))
          .unsafeRunSync()
      } finally {
        if (temp.exists()) temp.delete()
        ()
      }
    } else {
      println(s"Golden: ${file} does not exist. Creating golden image.")
      image.write[Png](file)
    }
  }

  def testImage(name: String)(image: Image)(implicit loc: Location) =
    test(name) {
      assertGoldenImage(name, image)
    }
}
