package doodle
package frp

import doodle.core._
import doodle.syntax._
import doodle.backend.StandardInterpreter._

object Demo {
  def run(implicit c: doodle.backend.Canvas) = {
    c.setSize(600, 600)
    Canvas.animationFrame.iota(0.0, 1.0, 0.01).map(x => x * x).map(x => {
        c.clear(Color.white)
        Circle(10).at(x * 600 - 300, 0).draw
      }
    )
  }
}
