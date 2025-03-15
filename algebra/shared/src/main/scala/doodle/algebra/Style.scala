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
package algebra

import doodle.core.Cap
import doodle.core.Color
import doodle.core.Gradient
import doodle.core.Join

/** Apply styling to a image.
  */
trait Style extends Algebra {
  def fillColor[A](image: Drawing[A], fillColor: Color): Drawing[A]
  def fillGradient[A](image: Drawing[A], fillGradient: Gradient): Drawing[A]
  def strokeColor[A](image: Drawing[A], strokeColor: Color): Drawing[A]
  def strokeGradient[A](image: Drawing[A], strokeGradient: Gradient): Drawing[A]  
  def strokeWidth[A](image: Drawing[A], strokeWidth: Double): Drawing[A]
  def strokeCap[A](image: Drawing[A], strokeCap: Cap): Drawing[A]
  def strokeJoin[A](image: Drawing[A], strokeJoin: Join): Drawing[A]

  /** Specify the stroke dash pattern. The pattern gives the length, in local
    * coordinates, of opaque and transparent sections. The first element is the
    * length of an opaque section, the second of a transparent section, and so
    * on.
    */
  def strokeDash[A](image: Drawing[A], pattern: Iterable[Double]): Drawing[A]
  def noDash[A](image: Drawing[A]): Drawing[A]

  def noFill[A](image: Drawing[A]): Drawing[A]
  def noStroke[A](image: Drawing[A]): Drawing[A]
}
