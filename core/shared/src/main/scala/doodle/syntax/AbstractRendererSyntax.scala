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
import doodle.effect.DefaultFrame
import doodle.effect.Renderer

/** Rendering works differently on different platforms. The Javascript runtime
  * must render asynchronously. The JVM runtime can render asychronously or
  * sychronously. However, rendering in a Swing / Java2D context takes places on
  * a daemon thread. This means the JVM will exit if this is the only thread
  * running. The implication is that a short Doodle program that does not block
  * the main thread waiting for the Swing thread to complete will usually exit
  * before the output appears. Therefore, at least in the common case of calling
  * `draw`, rendering should be synchronous on the JVM.
  */
trait AbstractRendererSyntax {

  /** Subtypes should implement this with unsafeRunSync or unsafeRunAsync as
    * appropriate. Returns Unit because unsafeRunAsync cannot return a value.
    */
  protected def runIO[A](io: IO[A])(implicit runtime: IORuntime): Unit

  implicit class RendererPictureOps[Alg <: Algebra, A](
      picture: Picture[Alg, A]
  ) {

    /** Convenience to immediately render a `Picture`, using the default `Frame`
      * options for this `Renderer`.
      */
    def draw[Frame, Canvas]()(implicit
        renderer: Renderer[Alg, Frame, Canvas],
        frame: DefaultFrame[Frame],
        r: IORuntime
    ): Unit =
      runIO(drawToIO())

    /** Convenience to immediately render a `Picture`, using the given `Frame`
      * options for this `Renderer`.
      */
    def drawWithFrame[Frame, Canvas](
        frame: Frame
    )(implicit renderer: Renderer[Alg, Frame, Canvas], r: IORuntime): Unit =
      runIO(drawWithFrameToIO(frame))

    /** Convenience to immediately render a `Picture`, using the given `Canvas`
      * for this `Renderer`.
      */
    def drawWithCanvas[Canvas](
        canvas: Canvas
    )(implicit renderer: Renderer[Alg, ?, Canvas], r: IORuntime): Unit =
      runIO(drawWithCanvasToIO(canvas))

    /** Create an effect that, when run, will draw `Picture` on the default
      * `Frame` for this `Renderer`.
      */
    def drawToIO[Frame, Canvas]()(implicit
        renderer: Renderer[Alg, Frame, Canvas],
        frame: DefaultFrame[Frame]
    ): IO[A] =
      renderer
        .canvas(frame.default)
        .flatMap(canvas => drawWithCanvasToIO(canvas))

    /** Create an effect that, when run, will draw the `Picture` using the given
      * `Frame` options.
      */
    def drawWithFrameToIO[Frame, Canvas](frame: Frame)(implicit
        renderer: Renderer[Alg, Frame, Canvas]
    ): IO[A] =
      renderer.canvas(frame).flatMap(canvas => drawWithCanvasToIO(canvas))

    /** Create an effect that, when run, will draw the `Picture` on the given
      * `Canvas`.
      */
    def drawWithCanvasToIO[Canvas](canvas: Canvas)(implicit
        renderer: Renderer[Alg, ?, Canvas]
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
