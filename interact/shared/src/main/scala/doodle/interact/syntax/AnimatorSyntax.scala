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
  implicit class AnimateObservableOps[Alg[x[_]] <: Algebra[x], F[_], A](
      frames: Observable[Picture[Alg, F, A]]) {
    /** Render an `Observable` that is generating frames an appropriate rate for animation. */
    def animate[Frame, Canvas](canvas: Canvas)(implicit a: Animator[Canvas],
                              e: Renderer[Alg, F, Frame, Canvas],
                              s: Scheduler,
                              m: Monoid[A]): Unit = {
      a.animate(canvas)(frames).unsafeRunAsync(v => println(v))
    }
  }

  implicit class AnimateToObservableOps[Alg[x[_]] <: Algebra[x], F[_], G[_], A](
      frames: G[Picture[Alg, F, A]]) {
    /** Animate a source of frames that is not producing those frames at a rate that is suitable for animation. */
    def animateFrames[Frame, Canvas](canvas: Canvas)(implicit a: Animator[Canvas],
                              e: Renderer[Alg, F, Frame, Canvas],
                              r: Redraw[Canvas],
                              s: Scheduler,
                              o: ObservableLike[G],
                              m: Monoid[A]): Unit = {
      val animatable: Observable[Picture[Alg, F, A]] =
        o(frames).zip(r.redraw(canvas)).map{ case (frame, _) => frame }
      a.animate(canvas)(animatable).unsafeRunAsync(v => println(v))
    }
  }
}
