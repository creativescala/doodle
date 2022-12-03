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
package syntax

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import doodle.algebra.Algebra
import doodle.algebra.Picture
import doodle.effect.DefaultRenderer
import doodle.effect.Renderer

trait RendererSyntax {

  def nullCallback[A](r: Either[Throwable, A]): Unit =
    r match {
      case Left(err) =>
        println("There was an error rendering a picture")
        err.printStackTrace()

      case Right(_) => ()
    }

  implicit class RendererPictureOps[Alg <: Algebra, A](
      picture: Picture[Alg, A]
  ) {

    /** Convenience to immediately render a `Picture`, using the default `Frame`
      * options for this `Renderer`.
      */
    def draw[Frame, Canvas](cb: Either[Throwable, A] => Unit = nullCallback _)(
        implicit
        renderer: DefaultRenderer[Alg, Frame, Canvas],
        r: IORuntime
    ): Unit =
      drawToIO.unsafeRunAsync(cb)

    /** Convenience to immediately render a `Picture`, using the given `Frame`
      * options for this `Renderer`.
      */
    def drawWithFrame[Frame, Canvas](
        frame: Frame,
        cb: Either[Throwable, A] => Unit = nullCallback _
    )(implicit renderer: Renderer[Alg, Frame, Canvas], r: IORuntime): Unit =
      (for {
        canvas <- renderer.canvas(frame)
        a <- renderer.render(canvas)(picture)
      } yield a).unsafeRunAsync(cb)

    /** Convenience to immediately render a `Picture`, using the given `Canvas`
      * for this `Renderer`.
      */
    def drawWithCanvas[Canvas](
        canvas: Canvas,
        cb: Either[Throwable, A] => Unit = nullCallback _
    )(implicit renderer: Renderer[Alg, _, Canvas], r: IORuntime): Unit =
      drawWithCanvasToIO(canvas).unsafeRunAsync(cb)

    /** Create an effect that, when run, will draw `Picture` on the default
      * `Frame` for this `Renderer`.
      */
    def drawToIO[Frame, Canvas](implicit
        renderer: DefaultRenderer[Alg, Frame, Canvas]
    ): IO[A] =
      (for {
        canvas <- renderer.canvas(renderer.default)
        a <- renderer.render(canvas)(picture)
      } yield a)

    /** Create an effect that, when run, will draw the `Picture` on the given
      * `Canvas`.
      */
    def drawWithCanvasToIO[Canvas](canvas: Canvas)(implicit
        renderer: Renderer[Alg, _, Canvas]
    ): IO[A] =
      renderer.render(canvas)(picture)
  }

  implicit class RendererFrameOps[Frame](frame: Frame) {
    def canvas[Alg <: Algebra, Canvas]()(implicit
        renderer: Renderer[Alg, Frame, Canvas]
    ): IO[Canvas] =
      renderer.canvas(frame)
  }
}
