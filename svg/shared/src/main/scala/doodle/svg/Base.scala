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

import doodle.algebra.generic.Finalized
import scalatags.generic.Bundle

import scala.collection.mutable

/** Base trait for SVG implementations, defining common types
  *
  * Used for ML-style modules to ensure the JVM and JS implementations make
  * consistent use of types, and the compiler can prove this.
  */
trait Base { self =>
  type Builder
  type FragT
  type Output <: FragT

  val bundle: Bundle[Builder, Output, FragT]

  type Tag = bundle.Tag
  type Attr = bundle.Attr

  /** The result is:
    *
    *   - the Tag that should be rendered for this Picture
    *
    *   - other Tags that should be rendered in a separate group. Used for
    *     gradients and font styles, which must be specified separately in SVG.
    *
    *   - the restul of evaluating the Picture
    */
  type SvgResult[A] = (Tag, mutable.Set[Tag], A)
  type Algebra <: doodle.algebra.Algebra { type Drawing[A] = self.Drawing[A] }
  type Drawing[A] = Finalized[SvgResult, A]
}
