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

package doodle.canvas.effect

import cats.effect.IO
import doodle.algebra.generic.Finalized
import doodle.canvas.Picture
import doodle.canvas.algebra.CanvasAlgebra
import doodle.canvas.algebra.CanvasDrawing
import doodle.core.Transform
import org.scalajs.dom

final case class Canvas(target: dom.Node, frame: Frame) {
  val canvas: dom.html.Canvas =
    dom.document.createElement("canvas").asInstanceOf[dom.html.Canvas]
  val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
  val algebra = CanvasAlgebra(ctx)

  target.appendChild(canvas)

  def render[A](picture: Picture[A]): IO[A] = {
    IO {
      val finalized: Finalized[CanvasDrawing, A] = picture(algebra)
      val (bb, rdr) = finalized.run(List.empty).value
      val tx =
        frame.size match {
          case Size.FitToPicture(border) =>
            val width = bb.width + (2 * border)
            val height = bb.height + (2 * border)

            // Work out the center of the bounding box, in logical local coordinates
            val centerX = bb.left + (bb.width / 2.0)
            val centerY = bb.bottom + (bb.height / 2.0)

            canvas.setAttribute("width", width.toString)
            canvas.setAttribute("height", height.toString)

            Transform
              .translate(-centerX, -centerY)
              .andThen(
                Transform.logicalToScreen(width, height)
              )

          case Size.FixedSize(width, height) =>
            canvas.setAttribute("width", width.toString)
            canvas.setAttribute("height", height.toString)

            Transform.logicalToScreen(width, height)
        }

      val drawing = rdr.runA(tx).value
      val a = drawing(ctx)
      a
    }
  }
}
object Canvas {
  def fromFrame(frame: Frame): IO[Canvas] = {
    IO {
      val target = dom.document.getElementById(frame.id)
      if (target == null) {
        throw new java.util.NoSuchElementException(
          s"Doodle Canvas could not be created, as could not find a DOM element with the requested id ${frame.id}"
        )
      } else Canvas(target, frame)
    }
  }
}
