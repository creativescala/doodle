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

package object docs {
  import java.io.File
  import cats.effect.unsafe.IORuntime
  import doodle.image.Image
  import doodle.core.format.*
  import doodle.java2d.*

  implicit class ImageSaveSyntax(image: Image) {
    import doodle.image.syntax.all.*
    def save(filename: String)(implicit r: IORuntime): Unit = {
      val dir = new File("docs/src/pages/")
      val file = new File(dir, filename)
      image.write[Png](file)
    }
  }

  implicit class PictureSaveSyntax(picture: Picture[Unit]) {
    import doodle.syntax.all.*
    def save(filename: String)(implicit r: IORuntime): Unit = {
      val dir = new File("docs/src/pages/")
      val file = new File(dir, filename)
      picture.write[Png](file)
    }
  }
}
