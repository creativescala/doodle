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
import doodle.core.Gradient.CycleMethod

object Gradients {
  def swatch(gradient: Gradient): Picture[Unit] =
    roundedRectangle(100, 100, 5)
      .fillGradient(gradient)
      .strokeWidth(1)
      .strokeColor(Color.black)

  extension (gradients: List[Gradient]) {
    def toSwatches: Picture[Unit] =
      gradients.map(g => swatch(g).margin(10, 0)).allBeside
  }

  val linearAndRadial =
    List(
      Gradient.linear(
        Point(-50, 0),
        Point(50, 0),
        Seq((Color.papayaWhip, 0.0), (Color.midnightBlue, 1.0)),
        CycleMethod.repeat
      ),
      Gradient.dichromaticRadial(Color.azure, Color.crimson, 70)
    ).toSwatches

  linearAndRadial.save("core/linear-radial.png")
}
