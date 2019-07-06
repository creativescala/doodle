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
import doodle.algebra.{Algebra,Picture}
import doodle.effect.{DefaultRenderer, Renderer}

trait RendererSyntax {

  def nullCallback[A](r: Either[Throwable, A]): Unit =
    r match {
      case Left(err) =>
        println("There was an error rendering a picture")
        err.printStackTrace()

      case Right(_) => ()
    }

  implicit class RendererPictureOps[Alg[x[_]] <: Algebra[x], F[_], A](
      picture: Picture[Alg, F, A]) {

    /** Convenience to immediately render a `Picture`, using the default `Frame` options for this `Renderer`. */
    def draw[Frame, Canvas](cb: Either[Throwable, A] => Unit = nullCallback _)(
        implicit renderer: DefaultRenderer[Alg, F, Frame, Canvas]): Unit =
      (for {
        canvas <- renderer.canvas(renderer.default)
        a <- renderer.render(canvas)(picture)
      } yield a).unsafeRunAsync(cb)

    /** Convenience to immediately render a `Picture`, using the given `Frame` options for this `Renderer`. */
    def drawToFrame[Frame, Canvas](frame: Frame, cb: Either[Throwable, A] => Unit = nullCallback _)(
        implicit renderer: Renderer[Alg, F, Frame, Canvas]): Unit =
      (for {
        canvas <- renderer.canvas(frame)
        a <- renderer.render(canvas)(picture)
       } yield a).unsafeRunAsync(cb)

    /** Convenience to immediately render a `Picture`, using the given `Canvas` for this `Renderer`. */
    def drawToCanvas[Canvas](canvas: Canvas, cb: Either[Throwable, A] => Unit = nullCallback _)(
        implicit renderer: Renderer[Alg, F, _, Canvas]): Unit =
      (for {
        a <- renderer.render(canvas)(picture)
       } yield a).unsafeRunAsync(cb)

    /** Convenience that passes through to the render method on Renderer. Nothing is rendered until the IO result is run. */
    def render[Canvas](canvas: Canvas)(implicit renderer: Renderer[Alg, F, _, Canvas]): IO[A] =
      renderer.render(canvas)(picture)
  }

  implicit class RendererFrameOps[Frame](frame: Frame) {
    def canvas[Alg[x[_]] <: Algebra[x], F[_], Canvas]()(
      implicit renderer: Renderer[Alg, F, Frame, Canvas]): IO[Canvas] =
      renderer.canvas(frame)
  }
}
