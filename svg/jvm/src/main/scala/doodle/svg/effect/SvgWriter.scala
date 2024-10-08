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
package svg
package effect

import cats.effect.IO
import doodle.algebra.Picture
import doodle.core.Base64 as B64
import doodle.core.format
import doodle.effect.*

import java.io.File
import java.nio.file.Files
import java.util.Base64 as JBase64

object SvgWriter
    extends FileWriter[Algebra, Frame, format.Svg]
    with Base64Writer[Algebra, Frame, format.Svg] {
  def write[A](
      file: File,
      description: Frame,
      picture: Picture[Algebra, A]
  ): IO[A] = {
    Svg
      .render[Algebra, A](description, algebraInstance, picture)
      .flatMap { case (nodes, a) =>
        IO {
          Files.write(file.toPath, nodes.getBytes())
          a
        }
      }
  }

  def base64[A](
      frame: Frame,
      image: Picture[Algebra, A]
  ): IO[(A, B64[format.Svg])] =
    for {
      rendered <- Svg
        .render[Algebra, A](frame, algebraInstance, image)
      (nodes, value) = rendered
      b64 = JBase64.getEncoder.encodeToString(nodes.getBytes())
    } yield (value, B64[format.Svg](b64))
}
