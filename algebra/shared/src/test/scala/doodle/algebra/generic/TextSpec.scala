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
package generic

import org.scalacheck.*
import org.scalacheck.Prop.*
import doodle.algebra.generic.StrokeStyle
import doodle.algebra.generic.reified.Reified
import doodle.core.Color

object TextSpec extends Properties("Text properties") {
  val algebra = TestAlgebra()

  property("strokeColor is preserved for text") = forAll(Generators.color) {
    (c) =>
      import Reified.*
      val reified =
        Generators.reify(algebra.strokeColor(algebra.text("Hello"), c))

      reified match {
        case List(textObj: Text) =>
          val stroke = textObj.stroke
          val text = textObj.text

          val strokeColor = stroke
            .map(s =>
              s.style match {
                case StrokeStyle.ColorStroke(col) => col
                case _ => Color.black // Default if not ColorStroke
              }
            )
            .getOrElse(Color.black)

          (strokeColor == c) && (text == "Hello")
        case _ => false
      }
  }
}
