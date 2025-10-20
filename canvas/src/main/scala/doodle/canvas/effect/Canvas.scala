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
import cats.effect.Ref
import cats.effect.Resource
import cats.syntax.all.*
import doodle.algebra.generic.Finalized
import doodle.canvas.Picture
import doodle.canvas.algebra.CanvasAlgebra
import doodle.canvas.algebra.CanvasDrawing
import doodle.core.Point
import doodle.core.Transform
import fs2.Stream
import org.scalajs.dom

final case class Canvas(
    target: dom.Node,
    frame: Frame,
    currentTx: Ref[IO, Transform],
    currentInverseTx: Ref[IO, Transform]
) {
  val canvas: dom.html.Canvas =
    dom.document.createElement("canvas").asInstanceOf[dom.html.Canvas]
  val context =
    canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
  val algebra = CanvasAlgebra(context)

  target.appendChild(canvas)

  private val eventListenerOptions = new dom.EventListenerOptions {}
  eventListenerOptions.once = true

  /** A stream containing an element each time the canvas is ready to be drawn
    * upon. Each element is the number of milliseconds that have expired since
    * the previous element. The underlying implementation uses the
    * `requestAnimationFrame` API.
    */
  val redraw: Stream[IO, Int] = {
    var started = false
    var lastTs = 0.0

    Stream.repeatEval(
      IO.async_ { cb =>
        dom.window.requestAnimationFrame(ts =>
          if started then {
            val diff = ts - lastTs
            lastTs = ts
            cb(Right(diff.toInt))
          } else {
            started = true
            cb(Right(0))
          }
        )
        ()
      }
    )
  }

  private def mouseEventToCurrentLocation(
      tx: Transform,
      evt: dom.MouseEvent
  ): Point = {
    val rect =
      evt.currentTarget.asInstanceOf[dom.Element].getBoundingClientRect()
    val x = evt.clientX - rect.left; // x position within the element.
    val y = evt.clientY - rect.top;
    tx(doodle.core.Point(x, y))
  }

  /** A stream of `Point` giving the current location of the mouse cursor in the
    * global Doodle coordinate system. The underlying implementation uses
    * "mousemove" events.
    */
  val mouseMove: Stream[IO, Point] = {
    Stream.repeatEval(
      currentInverseTx.get.flatMap { tx =>
        IO.async_(cb =>
          canvas.addEventListener(
            "mousemove",
            evt =>
              cb(
                Right(
                  mouseEventToCurrentLocation(
                    tx,
                    evt.asInstanceOf[dom.MouseEvent]
                  )
                )
              ),
            eventListenerOptions
          )
        )
      }
    )
  }

  /** A stream of `Point` giving the location of mouse clicks in the global
    * Doodle coordinate system. The underlying implementation uses "click"
    * events.
    */
  val mouseClick: Stream[IO, Point] = {
    Stream.repeatEval(
      currentInverseTx.get.flatMap { tx =>
        IO.async_(cb =>
          canvas.addEventListener(
            "click",
            evt =>
              cb(
                Right(
                  mouseEventToCurrentLocation(
                    tx,
                    evt.asInstanceOf[dom.MouseEvent]
                  )
                )
              ),
            eventListenerOptions
          )
        )
      }
    )
  }

  def render[A](picture: Picture[A]): IO[A] = {
    IO {
      val finalized: Finalized[CanvasDrawing, A] = picture(algebra)
      val (bb, rdr) = finalized.run(List.empty).value
      val (tx, inverseTx) =
        frame.size match {
          case Size.FitToPicture(border) =>
            val width = bb.width + (2 * border)
            val height = bb.height + (2 * border)

            // Work out the center of the bounding box, in logical local coordinates
            val centerX = bb.left + (bb.width / 2.0)
            val centerY = bb.bottom + (bb.height / 2.0)

            canvas.setAttribute("width", width.toString)
            canvas.setAttribute("height", height.toString)

            val tx =
              Transform
                .translate(-centerX, -centerY)
                .andThen(
                  Transform.logicalToScreen(width, height)
                )

            val inverseTx = Transform.screenToLogical(width, height)

            (tx, inverseTx)

          case Size.FixedSize(width, height) =>
            canvas.width = width.toInt
            canvas.height = height.toInt

            val tx = Transform.logicalToScreen(width, height)
            val inverseTx = Transform.screenToLogical(width, height)

            (tx, inverseTx)
        }

      frame.background.foreach { color =>
        context.fillStyle = CanvasDrawing.colorToCSS(color)
        context.fillRect(0, 0, canvas.width, canvas.height)
      }

      val drawing = rdr.runA(tx).value
      val a = drawing(context)
      (tx, inverseTx, a)
    }.flatMap { (tx, inverseTx, a) =>
      (currentTx.set(tx) >> currentInverseTx.set(inverseTx)).as(a)
    }
  }
}
object Canvas {
  def fromFrame(frame: Frame): Resource[IO, Canvas] = {
    (
      Ref.of[IO, Transform](Transform.identity),
      Ref.of[IO, Transform](Transform.identity)
    ).tupled
      .flatMap { (currentTx, currentInverseTx) =>
        IO {
          val target = dom.document.getElementById(frame.id)
          if target == null then {
            throw new java.util.NoSuchElementException(
              s"Doodle Canvas could not be created, as could not find a DOM element with the requested id ${frame.id}"
            )
          } else Canvas(target, frame, currentTx, currentInverseTx)
        }
      }
  }.toResource
}
