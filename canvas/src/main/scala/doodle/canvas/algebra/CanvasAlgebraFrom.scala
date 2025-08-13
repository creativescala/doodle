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

import cats.data.State
import doodle.algebra.generic.Finalized
import doodle.core.BoundingBox
import doodle.core.Transform as Tx
import org.scalajs.dom

/** Generic algebra that converts some type into a CanvasDrawing. */
trait CanvasAlgebraFrom extends doodle.algebra.Algebra {
  type Drawing[A] = Finalized[CanvasDrawing, A]

  protected def fromImage(
      image: dom.HTMLElement,
      width: Double,
      height: Double
  ): Drawing[Unit] =
    Finalized.leaf { dc =>
      val w = width
      val h = height
      val bb = BoundingBox.centered(w, h)
      (
        bb,
        State.inspect(tx =>
          CanvasDrawing.setTransform(
            Tx.verticalReflection.andThen(tx)
          ) >> CanvasDrawing.drawImage(image, w, h)
        )
      )
    }
}

trait FromHtmlImageElement { self: CanvasAlgebraFrom =>

  /** Create a picture from an HTMLImageElement.
    */
  def fromHtmlImageElement(image: dom.HTMLImageElement): Drawing[Unit] =
    fromImage(image, image.width, image.height)
}

trait FromImageBitmap { self: CanvasAlgebraFrom =>

  /** Create a picture from an ImageBitmap.
    */
  def fromImageBitmap(image: dom.ImageBitmap): Drawing[Unit] =
    fromImage(image.asInstanceOf[dom.HTMLElement], image.width, image.height)
}
