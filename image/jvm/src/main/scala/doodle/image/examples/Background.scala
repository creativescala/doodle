package doodle
package image
package examples

// Example that demonstrates setting the background color
import doodle.core._
import doodle.syntax._
import doodle.java2d.effect.Frame

object Background {
  val frame = Frame.fitToPicture().background(Color.black)

  def rainbowCircles(count: Int, color: Color): Image =
    count match {
      case 0 => Image.empty
      case n =>
        val here =
          Image
            .circle((n * 10).toDouble)
            .strokeWidth(3.0)
            .strokeColor(color)
        here.on(rainbowCircles(n - 1, color.spin(33.degrees)))
    }

  // Draw with `Background.image.draw(Background.frame)`
  val image = rainbowCircles(12, Color.red)
}
