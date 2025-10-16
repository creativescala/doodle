package doodle.examples.reactor

import cats.effect.unsafe.implicits.global
import doodle.core.*
import doodle.svg.*
import doodle.syntax.all.*
import doodle.image.*
import doodle.reactor.*
import doodle.reactor.syntax.all.*

import scala.scalajs.js.annotation.*

@JSExportTopLevel("ReactorFollowing")
object Following {
  final case class State(point: Point, color: Color)

  val reactor =
    Reactor
      .init(State(Point.zero, Color.red))
      .withOnMouseMove((pt, state) => state.copy(point = pt))
      .withOnMouseClick((_, state) =>
        state.copy(color = state.color.spin(15.degrees))
      )
      .withRender(state =>
        Image.circle(20).noStroke.fillColor(state.color).at(state.point)
      )

  @JSExport
  def go(id: String): Unit =
    reactor.animateWithFrame(Frame(id).withSize(300, 300))
}
