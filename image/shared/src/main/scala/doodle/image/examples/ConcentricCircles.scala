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
package image
package examples

import doodle.core.*
import doodle.syntax.all.*

object ConcentricCircles {
  def fade(n: Int): Image =
    singleCircle(n).strokeColor(Color.red fadeOut (n / 20.0).normalized)

  def gradient(n: Int): Image =
    singleCircle(n).strokeColor(Color.royalBlue.spin((n * 15).degrees))

  def singleCircle(n: Int): Image =
    Image.circle(50.0 + 7 * n).strokeWidth(3.0)

  def concentricCircles(n: Int): Image =
    n match {
      case 0 => singleCircle(n)
      case n => singleCircle(n) on concentricCircles(n - 1)
    }

  def fadeCircles(n: Int): Image =
    n match {
      case 0 => fade(n)
      case n => fade(n) on fadeCircles(n - 1)
    }

  def gradientCircles(n: Int): Image =
    n match {
      case 0 => gradient(n)
      case n => gradient(n) on gradientCircles(n - 1)
    }

  def image: Image =
    concentricCircles(20).strokeColor(Color.royalBlue)

  def fade: Image =
    fadeCircles(20)

  def gradient: Image =
    gradientCircles(20)
}
