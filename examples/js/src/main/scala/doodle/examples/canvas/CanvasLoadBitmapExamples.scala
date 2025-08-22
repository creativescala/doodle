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

package doodle.examples.canvas

import cats.effect.unsafe.implicits.global
import doodle.canvas.{*, given}
import doodle.syntax.all.*
import org.scalajs.dom

import scala.scalajs.js.annotation.*

@JSExportTopLevel("CanvasLoadBitmapExamples")
object CanvasLoadBitmapExamples {

  // Small test image (3x3 pixels)
  val testImageUrl =
    "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAADCAIAAADZSiLoAAAAF0lEQVR4nGNkYPjPwMDAwMDAxAADCBYAG10BBdmz9y8AAAAASUVORK5CYII="

  // Scaled down version of https://commons.wikimedia.org/wiki/File:A_Koch_woman.jpg
  //
  // Used under CC license https://creativecommons.org/licenses/by-sa/4.0/deed.en
  val wikimediaUrl =
    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/aa/A_Koch_woman.jpg/330px-A_Koch_woman.jpg"

  @JSExport
  def demo(id: String): Unit = {
    val program = for {
      htmlImg <- wikimediaUrl.loadBitmap[dom.HTMLImageElement].toPicture
      imgBitmap <- wikimediaUrl.loadBitmap[dom.ImageBitmap].toPicture

      composite = htmlImg.beside(imgBitmap)

      _ <- composite.drawWithFrameToIO(Frame(id))
    } yield ()

    val _ = program.unsafeToFuture()
  }
}
