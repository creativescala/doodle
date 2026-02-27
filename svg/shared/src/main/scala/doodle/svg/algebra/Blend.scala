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

package doodle
package svg
package algebra

import doodle.algebra.generic.*

trait BlendModule { root: Base with SvgModule =>
  trait Blend extends GenericBlend[SvgResult] {
    self: doodle.algebra.Algebra {
      type Drawing[A] = doodle.algebra.generic.Finalized[SvgResult, A]
    } =>

    object BlendApi extends BlendApi {
      def applyBlend[A](
          image: Finalized[SvgResult, A],
          blendMode: BlendMode
      ): Finalized[SvgResult, A] = {
        val mode = blendMode.toCssName
        image.flatMap { (bb, rdr) =>
          Finalized.leaf { dc =>
            val newRdr: Renderable[SvgResult, A] = rdr.map { result =>
              val (tag, defs, a) = result

              val b = bundle
              import b.implicits.*
              import b.{svgAttrs, svgTags}

              val blendedTag = svgTags.g(
                svgAttrs.style := s"mix-blend-mode: $mode;",
                tag
              )

              (blendedTag, defs, a)
            }

            (bb, newRdr)
          }
        }
      }
    }
  }
}
