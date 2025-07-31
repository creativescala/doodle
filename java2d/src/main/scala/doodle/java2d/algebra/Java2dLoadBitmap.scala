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
package algebra

import cats.effect.IO
import doodle.algebra.FileNotFound
import doodle.algebra.InvalidFormat
import doodle.algebra.LoadBitmap

import java.awt.image.BufferedImage
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.Path
import javax.imageio.ImageIO

object Java2dLoadBitmap {
  val loadBitmapFromFile: LoadBitmap[File, BufferedImage] =
    new LoadBitmap[File, BufferedImage] {
      def load(file: File): IO[BufferedImage] =
        IO {
          if !file.exists() then {
            throw new FileNotFoundException(s"File not found: ${file.getPath}")
          }

          Option(ImageIO.read(file)) match {
            case Some(image) => image
            case None        =>
              throw new IOException(
                s"Unable to read image from file: ${file.getPath}"
              )
          }
        }.adaptError {
          case _: FileNotFoundException =>
            FileNotFound(file.getPath)
          case e: IOException =>
            InvalidFormat(file.getPath, e)
          case e: Exception =>
            InvalidFormat(file.getPath, e)
        }
    }

  val loadBitmapFromPath: LoadBitmap[Path, BufferedImage] =
    new LoadBitmap[Path, BufferedImage] {
      def load(path: Path): IO[BufferedImage] =
        loadBitmapFromFile.load(path.toFile)
    }
}
