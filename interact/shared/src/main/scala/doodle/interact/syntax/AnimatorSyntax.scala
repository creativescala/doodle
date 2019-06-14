package doodle
package interact
package syntax

import cats.Monoid
import doodle.algebra.{Algebra,Picture}
import doodle.effect.Renderer
import doodle.interact.effect.Animator
import monix.reactive.Observable

trait AnimatorSyntax {
  implicit class AnimateObservableOps[Alg[x[_]] <: Algebra[x], F[_], A](
      frames: Observable[Picture[Alg, F, A]]) {
    def animate[Frame, Canvas](canvas: Canvas)(implicit a: Animator[Canvas],
                              e: Renderer[Alg, F, Frame, Canvas],
                              m: Monoid[A]): Unit = {
      a.animate(canvas)(frames).unsafeRunAsync(v => println(v))
    }
  }
}
