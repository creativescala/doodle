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
package golden

import cats.effect.unsafe.implicits.global
import doodle.algebra.Algebra
import doodle.algebra.Picture
import doodle.core.format._
import doodle.effect.Writer
import doodle.java2d._
import munit._

trait GoldenPicture extends Golden { self: FunSuite =>
  import doodle.syntax.all._

  def assertGoldenPicture[Alg <: Algebra](
      name: String,
      picture: Picture[Alg, Unit],
      frame: Frame = Frame.default.withSizedToPicture()
  )(implicit loc: Location, w: Writer[Alg, Frame, Png]) = {
    import java.io.File
    val file = new File(s"${goldenDir}/${name}.png")

    if (file.exists()) {
      val temp = new File(s"${goldenDir}/${name}.tmp.png")

      try {
        picture.write[Png](temp, frame)

        imageDiff(file, temp)
      } finally {
        if (temp.exists())
          temp.delete()
          ()
      }
    } else {
      println(s"Golden: ${file} does not exist. Creating golden image.")
      picture.write[Png](file, frame)
    }
  }

  def testPicture[Alg <: Algebra, A](name: String)(
      picture: Picture[Alg, Unit]
  )(implicit loc: Location, w: Writer[Alg, Frame, Png]) =
    test(name) {
      assertGoldenPicture(name, picture)
    }

  def testPictureWithFrame[Alg <: Algebra, A](name: String)(
      frame: Frame
  )(
      picture: Picture[Alg, Unit]
  )(implicit loc: Location, w: Writer[Alg, Frame, Png]) =
    test(name) {
      assertGoldenPicture(name, picture, frame)
    }
}
