package doodle
package interact
package syntax

import cats.Monoid
import doodle.algebra.{Algebra,Picture}
import doodle.effect.Renderer
import doodle.interact.algebra.Redraw
import doodle.interact.effect.Animator
import monix.execution.Scheduler
import monix.reactive.{Observable,ObservableLike}

trait AnimatorSyntax {
  def nullCallback[A](r: Either[Throwable, A]): Unit =
    r match {
      case Left(err) =>
        println("There was an error rendering an animation")
        err.printStackTrace()

      case Right(_) => ()
    }

  implicit class AnimateObservableOps[Alg[x[_]] <: Algebra[x], F[_], A](
      frames: Observable[Picture[Alg, F, A]]) {

    /** Render an `Observable` that is generating frames an appropriate rate for animation. */
    def animate[Frame, Canvas](frame: Frame, cb: Either[Throwable, A] => Unit = nullCallback _ )(implicit a: Animator[Canvas],
                          e: Renderer[Alg, F, Frame, Canvas],
                          s: Scheduler,
                                                                                                 m: Monoid[A]): Unit =
      (for {
        canvas <- e.canvas(frame)
        result <- a.animate(canvas)(frames)
       } yield result).unsafeRunAsync(cb)

    /** Render an `Observable` that is generating frames an appropriate rate for animation. */
    def animateToCanvas[Canvas](canvas: Canvas, cb: Either[Throwable, A] => Unit = nullCallback _)(implicit a: Animator[Canvas],
                              e: Renderer[Alg, F, _, Canvas],
                              s: Scheduler,
                              m: Monoid[A]): Unit = {
      a.animate(canvas)(frames).unsafeRunAsync(cb)
    }
  }

  implicit class AnimateToObservableOps[Alg[x[_]] <: Algebra[x], F[_], G[_], A](
      frames: G[Picture[Alg, F, A]]) {
    /** Animate a source of frames that is not producing those frames at a rate that is suitable for animation. */
    def animateFrames[Frame, Canvas](frame: Frame, cb: Either[Throwable, A] => Unit = nullCallback _)(implicit a: Animator[Canvas],
                              e: Renderer[Alg, F, Frame, Canvas],
                              r: Redraw[Canvas],
                              s: Scheduler,
                              o: ObservableLike[G],
                              m: Monoid[A]): Unit = {
      (for {
        canvas <- e.canvas(frame)
        animatable = o(frames).zip(r.redraw(canvas)).map{ case (frame, _) => frame }
        result <- a.animate(canvas)(animatable)
       } yield result).unsafeRunAsync(cb)
    }

    def animateFramesToCanvas[Canvas](canvas: Canvas, cb: Either[Throwable, A] => Unit = nullCallback _)(implicit a: Animator[Canvas],
                              e: Renderer[Alg, F, _, Canvas],
                              r: Redraw[Canvas],
                              s: Scheduler,
                              o: ObservableLike[G],
                              m: Monoid[A]): Unit = {
      val animatable: Observable[Picture[Alg, F, A]] =
        o(frames).zip(r.redraw(canvas)).map{ case (frame, _) => frame }

      animatable.animateToCanvas(canvas, cb)
    }
  }
}
