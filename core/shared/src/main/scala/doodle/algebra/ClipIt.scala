package doodle
package algebra

import doodle.core.ClosedPath
import doodle.algebra.Picture


trait ClipIt extends Algebra {
    def clipit[A](image: Drawing[A], clip_path: ClosedPath): Drawing[A]
}

trait ClipItConstructor {
    self: BaseConstructor { type Algebra <: ClipIt } =>

    def clipit(image: Picture[Unit], clip_path: ClosedPath): Picture[Unit] =
        new Picture[Unit] {
            def apply(implicit algebra: Algebra): algebra.Drawing[Unit] =
                algebra.clipit(image(algebra), clip_path)
        }
}
