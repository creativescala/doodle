package doodle
package image
package examples

// Example that demonstrates writing to a file
import doodle.core._
import doodle.syntax._
import doodle.image.syntax._
import doodle.effect.Writer._
import doodle.java2d._
import doodle.java2d.effect.Frame

object Write extends App {
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
  image.write[Png]("rainbow-circles.png", frame)
  println(image.write[Png].base64)
}
