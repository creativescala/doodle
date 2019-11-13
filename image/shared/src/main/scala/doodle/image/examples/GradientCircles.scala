package doodle
package image
package examples

import doodle.core._
import doodle.image.Image

object GradientCircles {
  val grad = Gradient.dichromaticRadial(Color.red, Color.blue, 100.0)

  val gradientCircle = Image.circle(100.0).fillGradient(grad)

  val image = gradientCircle above gradientCircle
}
