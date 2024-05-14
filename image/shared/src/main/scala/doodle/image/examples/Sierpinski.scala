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

object Sierpinski {
  def triangle(size: Double): Image = {
    println(s"Creating a triangle")
    Image.equilateralTriangle(size).strokeColor(Color.magenta)
  }

  def sierpinski(n: Int, size: Double): Image = {
    println(s"Creating a Sierpinski with n = $n")
    if n == 1 then {
      triangle(size)
    } else {
      val smaller = sierpinski(n - 1, size / 2)
      smaller.above(smaller.beside(smaller))
    }
  }

  val image = sierpinski(10, 512)
}
