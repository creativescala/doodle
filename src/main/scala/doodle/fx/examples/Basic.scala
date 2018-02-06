/*
 * Copyright 2015 noelwelsh
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
package fx
package examples

object Basic {
  import doodle.fx._
  import doodle.syntax._

  val squares = Image{ implicit algebra =>
    import algebra._
    val sq1 = fillColor(square(100), red)
    val sq2 = (fillColor(square(150), green)).lighten
    val sq3 = (fillColor(square(200), blue)).screen
    val squares = sq1.beside(sq2 on sq3)

    squares
  }

  def go(): Unit = {
    // Application.frame[Unit](Frame.size(400, 400).title("Square Fantasia")){ algebra =>
    //   draw(algebra)
    // }.unsafeRunSync()
    (squares above squares).draw
  }
}
