package doodle
package chart
package syntax

import doodle.core.Image
import doodle.backend.{Configuration, Draw, Save}
import doodle.backend.{Interpreter => BackendInterpreter}

trait ChartSyntax {
  implicit class ChartOps[A](chart: Chart) {
    def asImage(implicit i: Interpreter): Image =
      i(chart)

    def draw(implicit ci: Interpreter, d: Draw, ii: Configuration => BackendInterpreter): Unit = {
      import doodle.syntax._
      chart.asImage.draw
    }

    def save[Format](fileName: String)(implicit ci: Interpreter, save: Save[Format], ii: Configuration => BackendInterpreter): Unit = {
      import doodle.syntax._
      chart.asImage.save[Format](fileName)
    }
  }
}
