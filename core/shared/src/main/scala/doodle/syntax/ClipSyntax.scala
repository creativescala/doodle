package doodle
package syntax

import doodle.algebra.Algebra
import doodle.algebra.Picture
import doodle.core.ClosedPath
import doodle.algebra.ClipIt

trait ClipSyntax {
  implicit class ClipOps[Alg <: ClipIt, A](
      picture: Picture[Alg, A]
  ) {
    def clipit(clip_path: ClosedPath): Picture[Alg, A] =
      new Picture[Alg, A] {
        def apply(implicit algebra: Alg): algebra.Drawing[A] =
          algebra.clipit(picture(algebra), clip_path)
      }
  }
}
