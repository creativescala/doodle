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

package docs
package core

import cats.effect.unsafe.implicits.global
import doodle.core.*
import doodle.java2d.*
import doodle.syntax.all.*

object Gradients {
  val box = Picture.rectangle(10, 40).noStroke

  def gradient(count: Int, step: Angle, f: Angle => Color): Picture[Unit] =
    if count == 0 then Picture.empty
    else box.fillColor(f(step * count)).beside(gradient(count - 1, step, f))

  val hsl =
    gradient(40, 5.degrees, angle => Color.hsl(angle, 0.5, 0.5))

  val oklch =
    gradient(40, 5.degrees, angle => Color.oklch(0.7, 0.2, angle + 36.degrees))

  val picture = hsl.above(oklch.margin(0, 20))

  picture.save("core/gradients.png")
}
