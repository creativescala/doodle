package doodle
package svg
package effect

import cats.Monoid
import cats.effect.IO
import doodle.effect.Renderer
import doodle.interact.effect.Animator
import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.{Consumer, Observable}

object SvgAnimator extends Animator[Canvas] {

  def animate[Alg[x[_]] <: doodle.algebra.Algebra[x], F[_], A, Frm](canvas: Canvas)(
    frames: Observable[doodle.algebra.Picture[Alg, F, A]])(
    implicit e: Renderer[Alg, F, Frm, Canvas],
    m: Monoid[A]): IO[A] =
    frames
      .mapEval{img => println("blasting a rendering job"); Task.fromIO(e.render(canvas)(img))}
      .consumeWith(Consumer.foldLeft(m.empty) { (accum, a) =>
                     println("combining like a boss")
        m.combine(accum, a)
      })
      .toIO(
        Task.catsEffect(Scheduler.trampoline())
      )
}
