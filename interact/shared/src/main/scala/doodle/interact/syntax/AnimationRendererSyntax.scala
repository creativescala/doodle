package doodle
package interact
package syntax

import cats.Monoid
import cats.effect.IO
import doodle.algebra.Algebra
import doodle.algebra.Picture
import doodle.effect.Renderer
import doodle.interact.algebra.Redraw
import doodle.interact.effect.AnimationRenderer
import monix.execution.Scheduler
import monix.reactive.Observable
import monix.reactive.ObservableLike

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

  implicit class AnimateObservableOps[Alg[x[_]] <: Algebra[x], F[_], A](
      frames: Observable[Picture[Alg, F, A]]
  ) {

    /** Makes this Observable produce frames with the given period between
      * frames. This is useful if the Observable is producing frames too quickly
      * or slowly for the desired animation.
      *
      * A convenience derived from the throttle method on Observable.
      */
    def withFrameRate(period: FiniteDuration): Observable[Picture[Alg, F, A]] =
      frames.throttle(period, 1)

    /** Create an effect that, when run, will render an `Observable` that is
      * generating frames an appropriate rate for animation.
      */
    def animateToIO[Frame, Canvas](frame: Frame)(implicit
        a: AnimationRenderer[Canvas],
        e: Renderer[Alg, F, Frame, Canvas],
        s: Scheduler,
        m: Monoid[A]
    ): IO[A] =
      (for {
        canvas <- e.canvas(frame)
        result <- a.animate(canvas)(frames)
      } yield result)

    /** Render an `Observable` that is generating frames an appropriate rate for
      * animation.
      */
    def animate[Frame, Canvas](
        frame: Frame,
        cb: Either[Throwable, A] => Unit = theNullCallback
    )(implicit
        a: AnimationRenderer[Canvas],
        e: Renderer[Alg, F, Frame, Canvas],
        s: Scheduler,
        m: Monoid[A]
    ): Unit =
      animateToIO(frame).unsafeRunAsync(cb)

    /** Create an effect that, when run, will render an `Observable` that is
      * generating frames an appropriate rate for animation.
      */
    def animateWithCanvasToIO[Canvas](canvas: Canvas)(implicit
        a: AnimationRenderer[Canvas],
        e: Renderer[Alg, F, _, Canvas],
        s: Scheduler,
        m: Monoid[A]
    ): IO[A] = {
      a.animate(canvas)(frames)
    }

    /** Render an `Observable` that is generating frames an appropriate rate for
      * animation.
      */
    def animateWithCanvas[Canvas](
        canvas: Canvas,
        cb: Either[Throwable, A] => Unit = theNullCallback
    )(implicit
        a: AnimationRenderer[Canvas],
        e: Renderer[Alg, F, _, Canvas],
        s: Scheduler,
        m: Monoid[A]
    ): Unit = {
      animateWithCanvasToIO(canvas).unsafeRunAsync(cb)
    }
  }

  implicit class AnimateToObservableOps[Alg[x[_]] <: Algebra[x], F[_], G[_], A](
      frames: G[Picture[Alg, F, A]]
  ) {

    /** Create an effect that, when run, will animate a source of frames that is
      * not producing those frames at a rate that is suitable for animation.
      */
    def animateFramesToIO[Frame, Canvas](frame: Frame)(implicit
        a: AnimationRenderer[Canvas],
        e: Renderer[Alg, F, Frame, Canvas],
        r: Redraw[Canvas],
        s: Scheduler,
        o: ObservableLike[G],
        m: Monoid[A]
    ): IO[A] = {
      (for {
        canvas <- e.canvas(frame)
        animatable = o(frames).zip(r.redraw(canvas)).map { case (frame, _) =>
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
        s: Scheduler,
        o: ObservableLike[G],
        m: Monoid[A]
    ): Unit = {
      animateFramesToIO(frame).unsafeRunAsync(cb)
    }

    def animateFramesWithCanvasToIO[Canvas](canvas: Canvas)(implicit
        a: AnimationRenderer[Canvas],
        e: Renderer[Alg, F, _, Canvas],
        r: Redraw[Canvas],
        s: Scheduler,
        o: ObservableLike[G],
        m: Monoid[A]
    ): IO[A] = {
      val animatable: Observable[Picture[Alg, F, A]] =
        o(frames).zip(r.redraw(canvas)).map { case (frame, _) => frame }

      animatable.animateWithCanvasToIO(canvas)
    }

    def animateFramesWithCanvas[Canvas](
        canvas: Canvas,
        cb: Either[Throwable, A] => Unit = theNullCallback
    )(implicit
        a: AnimationRenderer[Canvas],
        e: Renderer[Alg, F, _, Canvas],
        r: Redraw[Canvas],
        s: Scheduler,
        o: ObservableLike[G],
        m: Monoid[A]
    ): Unit = {
      animateFramesWithCanvasToIO(canvas).unsafeRunAsync(cb)
    }
  }
}
