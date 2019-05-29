package doodle

import doodle.effect.Renderer
import doodle.interact.algebra.MouseOver
import doodle.interact.effect.Animator
import doodle.language.Basic
import org.scalajs.dom
import scalatags.generic.TypedTag

package object svg {
  type Algebra[F[_]] = doodle.algebra.Algebra[F] with Basic[F] with MouseOver[F]
  type Tag = TypedTag[dom.Element, dom.Element, dom.Node]
  type SvgResult[A] = (Tag, A)
  type Drawing[A] = doodle.algebra.generic.Finalized[SvgResult, A]

  type Frame = doodle.svg.effect.Frame
  type Canvas = doodle.svg.effect.Canvas
  implicit val svgRenderer: Renderer[Algebra, Drawing, Frame, Canvas] =
    doodle.svg.effect.SvgRenderer
  implicit val svgAnimator: Animator[Canvas] =
    doodle.svg.effect.SvgAnimator

  val Frame = doodle.svg.effect.Frame

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
