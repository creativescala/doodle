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

/** This algebra allows you to include any ScalaTags tag within an SVG drawing.
  */
trait Tagged[Tag] extends Algebra {

  /** Wrap the given Tag around the given Drawing. */
  def tagged[A](drawing: Drawing[A], tag: Tag): Drawing[A]

  /** A utility to include wrap a link (an a tag) around a Drawing. */
  def link[A](drawing: Drawing[A], href: String): Drawing[A]
}
