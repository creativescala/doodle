package doodle
package java2d

import doodle.core.*
import doodle.syntax.all.*
import cats.effect.unsafe.implicits.global

/** A simple test to verify Java2D supports gradient strokes */
object GradientStrokeTest {
  def main(args: Array[String]): Unit = {
    // Linear gradient stroke example
    val linearGradientExample =
      square(200)
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
        .above(
          text("Linear Gradient Stroke").fillColor(Color.black)
        )

    // Radial gradient stroke example
    val radialGradientExample =
      circle(200)
        .strokeGradient(
          Gradient.radial(
            Point(0, 0),
            Point(0, 0),
            150,
            List(
              (
                Color.magenta,
                0.3
              ), // Start gradient closer to edge for visibility
              (Color.cyan, 1.0)
            ),
            Gradient.CycleMethod.noCycle
          )
        )
        .strokeWidth(20)
        .noFill
        .above(
          text("Radial Gradient Stroke").fillColor(Color.black)
        )

    // Put them side by side
    val examples = linearGradientExample.beside(radialGradientExample)

    // Draw the examples - ERROR WAS HERE - don't pass a string argument
    examples.draw()
  }
}
