package doodle
package image
package examples

// Example that demonstrates writing to a file
import cats.effect.unsafe.implicits.global
import doodle.core._
import doodle.core.format._
import doodle.image.syntax.all._
import doodle.java2d._
import doodle.java2d.effect.Frame
import doodle.syntax.all._

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
  image.write[Pdf]("rainbow-circles.pdf", frame)
  // Print base64 encoded png
  println(image.base64[Png])
}
