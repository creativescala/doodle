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

package doodle.examples.canvas

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

import cats.effect.unsafe.implicits.global
import doodle.canvas.{*, given}
import doodle.core.*
import doodle.syntax.all.*

import scala.scalajs.js.annotation.*

// @JSExportTopLevel("Example2")
// object ParametricSpiral {

//   def parametricSpiral(angle: Angle): Point =
//     Point((Math.exp(angle.toTurns) - 1) * 200, angle)

//   def drawCurve(
//       points: Int,
//       marker: Point => Picture[Unit],
//       curve: Angle => Point
//   ): Picture[Unit] = {
//     // Angle.one is one complete turn. I.e. 360 degrees
//     val turn = Angle.one / points.toDouble
//     def loop(count: Int): Picture[Unit] = {
//       count match {
//         case 0 =>
//           val pt = curve(Angle.zero)
//           marker(pt).at(pt)
//         case n =>
//           val pt = curve(turn * count.toDouble)
//           marker(pt).at(pt).on(loop(n - 1))
//       }
//     }

//     loop(points)
//   }

//   @JSExport
//   def draw(id: String): Unit = {
//     val marker = (point: Point) =>
//       Picture
//         .circle(point.r * 0.125 + 7)
//         .fillColor(Color.red.spin(point.angle / 4.0))
//         .noStroke

//     drawCurve(20, marker, parametricSpiral _).drawWithFrame(Frame(id))
//   }
// }

@JSExportTopLevel("Example2")
object Rectangle2 {
  val rect = Picture.rectangle(50, 10).fillColor(Color.blue)
  val rect2 = Picture.rectangle(50, 10).fillColor(Color.red)

  val joint = rect.below(rect2)

  // val immediate = (gc: GraphicsContext) => {
  //   gc.setColor(Color.red)
  //   gc.fillRect(0, 0, 50, 10)
  //   gc.setColor(Color.blue)
  //   gc.fillRect(0, 10, 50, 10)
  // }

  @JSExport
  def draw(mount: String) =
    joint.drawWithFrame(Frame(mount))
}
