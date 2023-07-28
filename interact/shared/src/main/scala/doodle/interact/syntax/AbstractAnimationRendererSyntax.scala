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
package interact
package syntax

import cats.Monoid
import cats.effect.IO
import cats.effect.unsafe.IORuntime
import doodle.algebra.Algebra
import doodle.algebra.Picture
import doodle.effect.Renderer
import doodle.interact.algebra.Redraw
import doodle.interact.effect.AnimationRenderer
import fs2.Stream

import scala.concurrent.duration.FiniteDuration

/** Rendering works differently on different platforms. The Javascript runtime
  * must render asynchronously. The JVM runtime can render asychronously or
  * sychronously. However, rendering in a Swing / Java2D context takes places on
  * a daemon thread. This means the JVM will exit if this is the only thread
  * running. The implication is that short Doodle program that does not block
  * the main thread waiting for the Swing thread to complete will usually exit
  * before the output appears. Therefore, at least in the common case, rendering
  * should be synchronous on the JVM. People who want more control can work
  * directly with `IO`.
  */
trait AbstractAnimationRendererSyntax {

  /** Subtypes should implement this with unsafeRunSync or unsafeRunAsync as
    * appropriate. Returns Unit because unsafeRunAsync cannot return a value.
    */
  protected def runIO[A](io: IO[A])(implicit runtime: IORuntime): Unit

  /** This syntax is for streams producing pictures at a rate that is
    * appropriate for animation.
    */
  implicit class AnimateStreamOps[Alg <: Algebra, A](
      frames: Stream[IO, Picture[Alg, A]]
  ) {

    /** Makes this Stream produce frames with the given period between frames.
      * This is useful if the Stream is producing frames too quickly or slowly
      * for the desired animation.
      *
      * A convenience derived from the `metered` method on `Stream`.
      */
    def withFrameRate(period: FiniteDuration): Stream[IO, Picture[Alg, A]] =
      frames.metered(period)

    /** Convenience to animate a `Stream` of pictures on a canvas created from
      * the given frame.
      */
    def animate[Frame, Canvas](frame: Frame)(implicit
        a: AnimationRenderer[Canvas],
        e: Renderer[Alg, Frame, Canvas],
        m: Monoid[A],
        runtime: IORuntime
    ): Unit =
      runIO(animateToIO(frame))

    /** Convenience to animate a `Stream` that is generating frames at an
      * appropriate rate for animation onto the given canvas.
      */
    def animateWithCanvas[Canvas](canvas: Canvas)(implicit
        a: AnimationRenderer[Canvas],
        e: Renderer[Alg, ?, Canvas],
        m: Monoid[A],
        runtime: IORuntime
    ): Unit = {
      runIO(animateWithCanvasToIO(canvas))
    }

    /** Create an effect that, when run, will render a `Stream` that is
      * generating frames at an appropriate rate for animation.
      */
    def animateToIO[Frame, Canvas](frame: Frame)(implicit
        a: AnimationRenderer[Canvas],
        e: Renderer[Alg, Frame, Canvas],
        m: Monoid[A]
    ): IO[A] =
      e.canvas(frame).flatMap(c => animateWithCanvasToIO(c))

    /** Create an effect that, when run, will render a `Stream` that is
      * generating frames an appropriate rate for animation.
      */
    def animateWithCanvasToIO[Canvas](canvas: Canvas)(implicit
        a: AnimationRenderer[Canvas],
        e: Renderer[Alg, ?, Canvas],
        m: Monoid[A]
    ): IO[A] = {
      a.animate(canvas)(frames)
    }
  }

  /** This syntax is for streams that are not producing pictures at a rate that
    * is appropriate for animation. They will be throttled to an appropriate
    * rate.
    */
  implicit class AnimateToStreamOps[Alg <: Algebra, A](
      frames: Stream[IO, Picture[Alg, A]]
  ) {

    /** Animate a source of frames that is not producing those frames at a rate
      * that is suitable for animation.
      */
    def animateFrames[Frame, Canvas](frame: Frame)(implicit
        a: AnimationRenderer[Canvas],
        e: Renderer[Alg, Frame, Canvas],
        r: Redraw[Canvas],
        m: Monoid[A],
        runtime: IORuntime
    ): Unit = {
      runIO(animateFramesToIO(frame))
    }

    /** Animate a source of frames that is not producing those frames at a rate
      * that is suitable for animation, displaying the animation on the given
      * canvas.
      */
    def animateFramesWithCanvas[Canvas](
        canvas: Canvas
    )(implicit
        a: AnimationRenderer[Canvas],
        e: Renderer[Alg, ?, Canvas],
        r: Redraw[Canvas],
        m: Monoid[A],
        runtime: IORuntime
    ): Unit = {
      runIO(animateFramesWithCanvasToIO(canvas))
    }

    /** Create an effect that, when run, will animate a source of frames that is
      * not producing those frames at a rate that is suitable for animation.
      */
    def animateFramesToIO[Frame, Canvas](frame: Frame)(implicit
        a: AnimationRenderer[Canvas],
        e: Renderer[Alg, Frame, Canvas],
        r: Redraw[Canvas],
        m: Monoid[A]
    ): IO[A] = {
      e.canvas(frame)
        .flatMap(c => animateFramesWithCanvasToIO(c))
    }

    /** Create an effect that, when run, will animate a source of frames that is
      * not producing those frames at a rate that is suitable for animation,
      * displaying the animation on the given canvas.
      */
    def animateFramesWithCanvasToIO[Canvas](canvas: Canvas)(implicit
        a: AnimationRenderer[Canvas],
        e: Renderer[Alg, ?, Canvas],
        r: Redraw[Canvas],
        m: Monoid[A]
    ): IO[A] = {
      val animatable: Stream[IO, Picture[Alg, A]] =
        frames.zip(r.redraw(canvas)).map { case (frame, _) => frame }

      animatable.animateWithCanvasToIO(canvas)
    }
  }
}
