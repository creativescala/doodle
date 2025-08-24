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

import doodle.algebra.Picture
import doodle.algebra.ToPicture

/** ToPicture implementation for SVG image references */
object SvgToPicture {

  /** Creates a ToPicture instance for SvgImageRef.
    *
    * Note on type constraint: We require svg.Algebra here, but in practice we
    * only use the `image` method from the Image trait. Any algebra that
    * includes ImageModule will work with this implementation.
    */
  def svgImageRefToPicture[Alg <: svg.Algebra]: ToPicture[SvgImageRef, Alg] =
    new ToPicture[SvgImageRef, Alg] {
      def toPicture(ref: SvgImageRef): Picture[Alg, Unit] =
        new Picture[Alg, Unit] {
          def apply(implicit algebra: Alg): algebra.Drawing[Unit] =
            algebra.image(ref)
        }
    }
}
