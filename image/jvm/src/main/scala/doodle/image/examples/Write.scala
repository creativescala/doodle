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

// Example that demonstrates writing to a file
import cats.effect.unsafe.implicits.global
import doodle.core._
import doodle.core.format._
import doodle.image.syntax.all._
import doodle.java2d._
import doodle.java2d.effect.Frame
import doodle.syntax.all._

object Write extends App {
  val frame = Frame.default.withSizedToPicture(20).withBackground(Color.black)

  def rainbowCircles(count: Int, color: Color): Image =
    count match {
      case 0 => Image.empty
      case n =>
        val here =
          Image
            .circle((n * 10).toDouble)
            .strokeWidth(3.0)
            .strokeColor(color)
        here.on(rainbowCircles(n - 1, color.spin(33.degrees)))
    }

  // Draw with `Background.image.draw(Background.frame)`
  val image = rainbowCircles(12, Color.red)
  image.write[Png]("rainbow-circles.png", frame)
  image.write[Pdf]("rainbow-circles.pdf", frame)
  // Print base64 encoded png
  println(image.base64[Png])
}
