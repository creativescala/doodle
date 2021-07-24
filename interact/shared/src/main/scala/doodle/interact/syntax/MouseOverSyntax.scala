package doodle
package interact
package syntax

import doodle.algebra.Picture
import doodle.interact.algebra.MouseOver
import monix.reactive.Observable
import monix.reactive.subjects.PublishSubject

trait MouseOverSyntax {
  implicit class MouseOverOps[F[_], A](picture: F[A]) {
    def mouseOver(implicit m: MouseOver[F]): (F[A], Observable[Unit]) =
      m.mouseOver(picture)
  }

  implicit class MouseOverPictureOps[Alg[x[_]] <: MouseOver[x], F[_], A](
      picture: Picture[Alg, F, A]
  ) {
    def mouseOver: (Picture[Alg, F, A], Observable[Unit]) = {
      val obs = PublishSubject[Unit]()
      val p = Picture { implicit algebra: Alg[F] =>
        val f1 = picture(algebra)
        val (f2, o) = f1.mouseOver
        o.map(obs.onNext _)
        f2
      }

      (p, obs)
    }
  }
}
