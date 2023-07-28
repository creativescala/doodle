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

import doodle.algebra.Layout
import doodle.algebra.Size
import doodle.algebra.Text
import doodle.algebra.generic._
import doodle.language.Basic

trait AlgebraModule {
  self: Base with ShapeModule with PathModule with SvgModule =>
  trait BaseAlgebra
      extends doodle.algebra.Algebra
      with Layout
      with Size
      with Shape
      with Path
      with Text
      with GenericDebug[SvgResult]
      with GenericLayout[SvgResult]
      with GenericSize[SvgResult]
      with GenericStyle[SvgResult]
      with GenericTransform[SvgResult]
      with GivenApply[SvgResult]
      with GivenFunctor[SvgResult]
      with Basic {
    type Drawing[A] = Finalized[SvgResult, A]
  }
}
