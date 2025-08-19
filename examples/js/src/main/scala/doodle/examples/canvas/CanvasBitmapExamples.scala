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
import doodle.core.*
import doodle.syntax.all.*
import org.scalajs.dom

import scala.scalajs.js.annotation.*

@JSExportTopLevel("CanvasLoadBitmapExamples")
object CanvasLoadBitmapExamples {

  // Small test image (3x3 pixels)
  val testImageUrl =
    "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAADCAIAAADZSiLoAAAAF0lEQVR4nGNkYPjPwMDAwMDAxAADCBYAG10BBdmz9y8AAAAASUVORK5CYII="

  @JSExport
  def demo(id: String): Unit = {
    val program = for {
      htmlImg <- testImageUrl.loadBitmap[dom.HTMLImageElement]
      imgBitmap <- testImageUrl.loadBitmap[dom.ImageBitmap]

      pic1 = htmlImg
        .toPicture[Algebra]
        .scale(50, 50)
        .strokeColor(Color.red)
        .strokeWidth(2.0)

      pic2 = imgBitmap
        .toPicture[Algebra]
        .scale(50, 50)
        .strokeColor(Color.blue)
        .strokeWidth(2.0)

      composite = pic1.beside(pic2)

      _ = composite.drawWithFrame(Frame(id))
    } yield ()

    val _ = program.unsafeToFuture()
  }
}
