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

package doodle.examples.svg

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import doodle.core.*
import doodle.interact.easing.*
import doodle.interact.syntax.all.*
import doodle.svg.*
import doodle.syntax.all.*
import fs2.Stream

import scala.concurrent.duration.*
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("SvgBouncyCircles")
object BouncyCircles {

  @JSExport
  def go(id: String) = {
    val frame = Frame(id).withSize(600, 600).withBackground(Color.darkMagenta)
    val steps = 60 * 10

    def bounce(easing: Easing): Stream[IO, Double] =
      easing.toStream(steps) ++
        easing.toStream(steps) ++
        easing.toStream(steps) ++
        easing.toStream(steps)

    val animation: Stream[IO, Picture[Unit]] =
      bounce(Easing.linear)
        .zip(bounce(Easing.quadratic))
        .zip(bounce(Easing.cubic))
        .zip(bounce(Easing.sin))
        .zip(bounce(Easing.circle))
        .zip(bounce(Easing.back))
        .map { case (((((r1, r2), r3), r4), r5), r6) =>
          circle(r1 * 85 + 10)
            .strokeColor(Color.magenta.spin(180.degrees))
            .at(r1 * 400 - 200, 250)
            .on(
              circle(r2 * 85 + 10)
                .strokeColor(Color.magenta.spin(170.degrees))
                .at(r2 * 400 - 200, 150)
            )
            .on(
              circle(r3 * 85 + 10)
                .strokeColor(Color.magenta.spin(140.degrees))
                .at(r3 * 400 - 200, 50)
            )
            .on(
              circle(r4 * 85 + 10)
                .strokeColor(Color.magenta.spin(150.degrees))
                .at(r4 * 400 - 200, -50)
            )
            .on(
              circle(r5 * 85 + 10)
                .strokeColor(Color.magenta.spin(140.degrees))
                .at(r5 * 400 - 200, -150)
            )
            .on(
              circle(r6 * 85 + 10)
                .strokeColor(Color.magenta.spin(120.degrees))
                .at(r6 * 400 - 200, -250)
            )
            .strokeWidth(2.0)
        }
        .evalTap(_ => IO.println("A frame"))

    animation.withFrameRate(20.milliseconds).animate(frame)
  }
}
