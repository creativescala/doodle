package doodle
package chart
package syntax

import doodle.core.Image

trait ChartSyntax {
  implicit class ChartOps[A](chart: Chart) {
    def asImage(implicit i: Interpreter): Image =
      i(chart)
  }
}
