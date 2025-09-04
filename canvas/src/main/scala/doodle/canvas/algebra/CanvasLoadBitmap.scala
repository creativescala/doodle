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

package doodle.canvas.algebra

import cats.effect.IO
import doodle.algebra.LoadBitmap
import doodle.algebra.NetworkError
import org.scalajs.dom.*

import scala.scalajs.js

/* Canvas implementation of LoadBitmap for loading images from URLs */
object CanvasLoadBitmap {

  /** LoadBitmap instance for loading images from URLs into HTMLImageElement */
  val loadBitmapFromUrl: LoadBitmap[String, HTMLImageElement] =
    new LoadBitmap[String, HTMLImageElement] {
      def load(url: String): IO[HTMLImageElement] =
        IO.async_ { callback =>
          val img = document.createElement("img").asInstanceOf[HTMLImageElement]

          img.addEventListener(
            "load",
            (_: Event) => {
              callback(Right(img))
            }
          )

          img.addEventListener(
            "error",
            (_: Event) => {
              callback(
                Left(
                  NetworkError(
                    url,
                    new Exception(s"Failed to load image from $url")
                  )
                )
              )
            }
          )

          img.src = url
        }
    }

  /** LoadBitmap instance for loading images from URLs as ImageBitmap */
  val loadBitMapFromUrlToImageBitmap: LoadBitmap[String, ImageBitmap] =
    new LoadBitmap[String, ImageBitmap] {
      def load(url: String): IO[ImageBitmap] =
        for {
          img <- loadBitmapFromUrl.load(url)
          bitmap <- IO.fromFuture(IO {
            window.createImageBitmap(img).toFuture
          })
        } yield bitmap
    }
}
