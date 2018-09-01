/*
 * Copyright 2015 noelwelsh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package doodle
package syntax

import cats.Traverse
import doodle.algebra.{Layout, Shape}
import doodle.image.Image

trait TraverseSyntax {
  implicit class TraverseOps[T[_],F[_]](val t: T[F[Unit]]) {
    import cats.instances.unit._

    def allOn(implicit layout: Layout[F], shape: Shape[F], traverse: Traverse[T]): F[Unit] =
      traverse.foldLeft(t, shape.empty){ (accum, img) => layout.on(accum, img) }

    def allBeside(implicit layout: Layout[F], shape: Shape[F], traverse: Traverse[T]): F[Unit] =
      traverse.foldLeft(t, shape.empty){ (accum, img) => layout.beside(accum, img) }

    def allAbove(implicit layout: Layout[F], shape: Shape[F], traverse: Traverse[T]): F[Unit] =
      traverse.foldLeft(t, shape.empty){ (accum, img) => layout.above(accum, img) }
  }

  implicit class TraverseImageOps[T[_]](val t: T[Image]) {
    def allOn(implicit traverse: Traverse[T]): Image =
      traverse.foldLeft(t, Image.empty){ (accum, img) => accum.on(img) }

    def allBeside(implicit traverse: Traverse[T]): Image =
      traverse.foldLeft(t, Image.empty){ (accum, img) => accum.beside(img) }

    def allAbove(implicit traverse: Traverse[T]): Image =
      traverse.foldLeft(t, Image.empty){ (accum, img) => accum.above(img) }
  }
}
