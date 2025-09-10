package doodle.svg.syntax

import doodle.algebra.Algebra
import doodle.algebra.Picture
import doodle.svg.algebra.Tagged

trait all {
  extension [Alg <: Algebra, A](picture: Picture[Alg, A]) {
    def tagged[Tag](tag: Tag): Picture[Alg & Tagged[Tag], A] =
      new Picture[Alg & Tagged[Tag], A] {
        def apply(implicit algebra: Alg & Tagged[Tag]): algebra.Drawing[A] =
          algebra.tagged(picture.apply(algebra), tag)
      }

    def link[Tag](href: String): Picture[Alg & Tagged[Tag], A] =
      new Picture[Alg & Tagged[Tag], A] {
        def apply(implicit algebra: Alg & Tagged[Tag]): algebra.Drawing[A] =
          algebra.link(picture.apply(algebra), href)
      }
  }
}
