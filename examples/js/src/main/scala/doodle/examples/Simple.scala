package doodle.examples

import doodle.core._
import doodle.interact._
import doodle.syntax.all._
import doodle.svg._
import doodle.interact.syntax.all._
import cats.effect.unsafe.implicits.global
import scala.scalajs.js.annotation._

@JSExportTopLevel("Simple")
object Simple {
  @JSExport
  def ball(id: String) =
    -100.0
      .upTo(100.0)
      .map(x =>
        Picture
          .circle(15)
          .fillColor(Color.chartReuse)
          .strokeWidth(3.0)
          .at(x, 0.0)
      )
      .forSteps(100)
      .repeatForever
      .animate(Frame(id).withSize(230, 30))
}
