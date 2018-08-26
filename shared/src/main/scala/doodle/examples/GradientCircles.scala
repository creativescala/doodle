package doodle.examples

import doodle.core._

object GradientCircles {

  val grad = Gradient.dichromaticRadial(Color.red, Color.blue, 100.0)

  val gradientCircle = Image.circle(100) fillGradient grad

  val image = gradientCircle above gradientCircle
}
