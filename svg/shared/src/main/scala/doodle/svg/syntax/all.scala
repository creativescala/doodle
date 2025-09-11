/*
 * Copyright 2015 Creative Scala
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
