package doodle
package chart
package syntax

import doodle.core.Image
import doodle.backend.{Configuration, Draw, Save}
import doodle.chart.interpreter.{Interpreter => ChartInterpreter}
import doodle.backend.{Interpreter => BackendInterpreter}

trait ChartSyntax {
  implicit class ChartOps[D <: DataType](chart: Chart[D]) {
    def asImage(style: Style = Style.default)(implicit i: ChartInterpreter[D]): Image =
      i(chart).run(style)

    def draw(implicit ci: ChartInterpreter[D], d: Draw, ii: Configuration => BackendInterpreter): Unit = {
      import doodle.syntax._
      chart.asImage().draw
    }

    def save[Format](fileName: String)(implicit ci: ChartInterpreter[D], save: Save[Format], ii: Configuration => BackendInterpreter): Unit = {
      import doodle.syntax._
      chart.asImage().save[Format](fileName)
    }
  }
}

