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

package doodle.svg.algebra

import doodle.algebra.Algebra
import scalatags.generic.AttrPair

/** This algebra allows you to include (ScalaTags) attributes within a SVG
  * drawing.
  *
  * The type parameter `Builder` corresponds to backend-specific `Builder` type
  * in ScalaTags.
  */
trait Attributed[Builder] extends Algebra {

  /** Add an attribute to the given Drawing. */
  def attribute[A, T](
      drawing: Drawing[A],
      attr: AttrPair[Builder, T]
  ): Drawing[A]

}
