package doodle

import doodle.language.Basic
import scalatags.Text

package object svg {
  type Algebra[F[_]] = doodle.algebra.Algebra[F] with Basic[F]
  type Drawing[A] = doodle.algebra.generic.Finalized[(Text.Tag, ?), A]

  type Frame = doodle.svg.effect.Frame
  implicit val svgWriter = doodle.svg.effect.SvgWriter

  type Picture[A] = doodle.algebra.Picture[Algebra, Drawing, A]
  object Picture {
    def apply(f: Algebra[Drawing] => Drawing[Unit]): Picture[Unit] = {
      new Picture[Unit] {
        def apply(implicit algebra: Algebra[Drawing]): Drawing[Unit] =
          f(algebra)
      }
    }
  }
}
