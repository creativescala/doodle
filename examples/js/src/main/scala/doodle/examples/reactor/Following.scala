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

package doodle.examples.reactor

import cats.effect.unsafe.implicits.global
import doodle.core.*
import doodle.image.*
import doodle.reactor.*
import doodle.reactor.syntax.all.*
import doodle.svg.*
import doodle.syntax.all.*

import scala.scalajs.js.annotation.*

@JSExportTopLevel("ReactorFollowing")
object Following {
  final case class State(point: Point, color: Color)

  val reactor =
    Reactor
      .init(State(Point.zero, Color.hotPink))
      .withOnMouseMove((pt, state) => state.copy(point = pt))
      .withRender(state =>
        Image
          .circle(20)
          .strokeColor(Color.white)
          .strokeWidth(3.0)
          .fillColor(state.color)
          .at(state.point)
      )

  @JSExport
  def go(id: String): Unit =
    reactor.animateWithFrame(
      Frame(id).withSize(300, 300).withBackground(Color.midnightBlue)
    )
}
