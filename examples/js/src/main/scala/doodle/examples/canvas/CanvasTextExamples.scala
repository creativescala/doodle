package doodle.examples.canvas

import cats.effect.unsafe.implicits.global
import doodle.examples.TextExamples
import doodle.canvas.{*, given}
import doodle.syntax.all.*

import scala.scalajs.js.annotation.*

@JSExportTopLevel("CanvasTextExamples")
object CanvasTextExamples extends TextExamples[Algebra] {
  @JSExport
  def drawHello(id: String): Unit = hello.drawWithFrame(Frame(id))

  @JSExport
  def drawFont(id: String): Unit = font.drawWithFrame(Frame(id))
}
