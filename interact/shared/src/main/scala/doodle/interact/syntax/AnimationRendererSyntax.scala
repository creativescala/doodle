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

trait AnimationRendererSyntax {
  def nullCallback[A](r: Either[Throwable, A]): Unit =
    r match {
      case Left(err) =>
        println("There was an error rendering an animation")
        err.printStackTrace()

      case Right(_) => ()
    }

  val theNullCallback = nullCallback _

  implicit class AnimateStreamOps[Alg[x[_]] <: Algebra[x], F[_], A](
      frames: Stream[IO, Picture[Alg, F, A]]
  ) {

    /** Makes this Stream produce frames with the given period between frames.
      * This is useful if the Stream is producing frames too quickly or slowly
      * for the desired animation.
      *
      * A convenience derived from the `metered` method on `Stream`.
      */
    def withFrameRate(period: FiniteDuration): Stream[IO, Picture[Alg, F, A]] =
      frames.metered(period)

    /** Create an effect that, when run, will render a `Stream` that is
      * generating frames an appropriate rate for animation.
      */
    def animateToIO[Frame, Canvas](frame: Frame)(implicit
        a: AnimationRenderer[Canvas],
        e: Renderer[Alg, F, Frame, Canvas],
        m: Monoid[A]
    ): IO[A] =
      (for {
        canvas <- e.canvas(frame)
        result <- a.animate(canvas)(frames)
      } yield result)

    /** Render a `Stream` that is generating frames an appropriate rate for
      * animation.
      */
    def animate[Frame, Canvas](
        frame: Frame,
        cb: Either[Throwable, A] => Unit = theNullCallback
    )(implicit
        a: AnimationRenderer[Canvas],
        e: Renderer[Alg, F, Frame, Canvas],
        m: Monoid[A],
        runtime: IORuntime
    ): Unit =
      animateToIO(frame).unsafeRunAsync(cb)

    /** Create an effect that, when run, will render a `Stream` that is
      * generating frames an appropriate rate for animation.
      */
    def animateWithCanvasToIO[Canvas](canvas: Canvas)(implicit
        a: AnimationRenderer[Canvas],
        e: Renderer[Alg, F, _, Canvas],
        m: Monoid[A]
    ): IO[A] = {
      a.animate(canvas)(frames)
    }

    /** Render a `Stream` that is generating frames at an appropriate rate for
      * animation.
      */
    def animateWithCanvas[Canvas](
        canvas: Canvas,
        cb: Either[Throwable, A] => Unit = theNullCallback
    )(implicit
        a: AnimationRenderer[Canvas],
        e: Renderer[Alg, F, _, Canvas],
        m: Monoid[A],
        runtime: IORuntime
    ): Unit = {
      animateWithCanvasToIO(canvas).unsafeRunAsync(cb)
    }
  }

  implicit class AnimateToStreamOps[Alg[x[_]] <: Algebra[x], F[_], A](
      frames: Stream[IO, Picture[Alg, F, A]]
  ) {

    /** Create an effect that, when run, will animate a source of frames that is
      * not producing those frames at a rate that is suitable for animation.
      */
    def animateFramesToIO[Frame, Canvas](frame: Frame)(implicit
        a: AnimationRenderer[Canvas],
        e: Renderer[Alg, F, Frame, Canvas],
        r: Redraw[Canvas],
        m: Monoid[A]
    ): IO[A] = {
      (for {
        canvas <- e.canvas(frame)
        animatable = frames.zip(r.redraw(canvas)).map { case (frame, _) =>
          frame
        }
        result <- a.animate(canvas)(animatable)
      } yield result)
    }

    /** Animate a source of frames that is not producing those frames at a rate
      * that is suitable for animation.
      */
    def animateFrames[Frame, Canvas](
        frame: Frame,
        cb: Either[Throwable, A] => Unit = theNullCallback
    )(implicit
        a: AnimationRenderer[Canvas],
        e: Renderer[Alg, F, Frame, Canvas],
        r: Redraw[Canvas],
        m: Monoid[A],
        runtime: IORuntime
    ): Unit = {
      animateFramesToIO(frame).unsafeRunAsync(cb)
    }

    def animateFramesWithCanvasToIO[Canvas](canvas: Canvas)(implicit
        a: AnimationRenderer[Canvas],
        e: Renderer[Alg, F, _, Canvas],
        r: Redraw[Canvas],
        m: Monoid[A]
    ): IO[A] = {
      val animatable: Stream[IO, Picture[Alg, F, A]] =
        frames.zip(r.redraw(canvas)).map { case (frame, _) => frame }

      animatable.animateWithCanvasToIO(canvas)
    }

    def animateFramesWithCanvas[Canvas](
        canvas: Canvas,
        cb: Either[Throwable, A] => Unit = theNullCallback
    )(implicit
        a: AnimationRenderer[Canvas],
        e: Renderer[Alg, F, _, Canvas],
        r: Redraw[Canvas],
        m: Monoid[A],
        runtime: IORuntime
    ): Unit = {
      animateFramesWithCanvasToIO(canvas).unsafeRunAsync(cb)
    }
  }
}
