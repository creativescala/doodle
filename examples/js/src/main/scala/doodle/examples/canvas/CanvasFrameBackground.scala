package doodle.examples.canvas

import cats.effect.unsafe.implicits.global
import doodle.core.Color
import doodle.canvas.{*, given}
import doodle.examples.LayoutExamples
import doodle.syntax.all.*

import scala.scalajs.js.annotation.*

@JSExportTopLevel("CanvasFrameBackground")
object CanvasFrameBackground {
  @JSExport
  def draw(id: String): Unit = {
    Picture.empty.drawWithFrame(
      Frame(id).withBackground(Color.midnightBlue).withSize(300, 300)
    )
  }
}
