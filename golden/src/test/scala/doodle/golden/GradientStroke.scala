package doodle
package golden

import doodle.core.*
import doodle.syntax.all.*
import doodle.java2d.*
import munit.*

class GradientStroke extends FunSuite with GoldenPicture {

  testPicture("linear-gradient-stroke") {
    Picture
      .square(200)
      .strokeGradient(
        Gradient.linear(
          Point(-100, -100),
          Point(100, 100),
          List(
            (Color.red, 0.0),
            (Color.yellow, 0.5),
            (Color.blue, 1.0)
          ),
          Gradient.CycleMethod.repeat
        )
      )
      .strokeWidth(20)
      .noFill
  }

  testPicture("radial-gradient-stroke") {
    Picture
      .circle(100)
      .strokeGradient(
        Gradient.radial(
          Point(0, 0),
          Point(0, 0),
          100,
          List(
            (Color.magenta, 0.3),
            (Color.cyan, 1.0)
          ),
          Gradient.CycleMethod.noCycle
        )
      )
      .strokeWidth(20)
      .noFill
  }
}
