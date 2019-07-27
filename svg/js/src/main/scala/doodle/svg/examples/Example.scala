package doodle
package svg
package examples

import scala.scalajs.js.annotation._
import doodle.core.Color
import doodle.svg._

// Wrapper to run an example in a web page
object Example {
  val frame =
    Frame("canvas")
      .size(600, 600)
      .background(Color.midnightBlue)

  @JSExportTopLevel("Main")
  def main(): Unit = {
    // Squares.squares.draw(frame)
    //
    // SierpinskiRipple.image.draw(frame)
    //
    // GradientCircle.image.draw(frame)
    //
    // import doodle.interact.syntax._
    // import cats.implicits._

    // Orbit.frames.animateFrames(frame)
    //
    import cats.instances.all._
    import doodle.syntax._
    import doodle.interact.syntax._
      (for {
         canvas <- frame.canvas
         frames <- Ripples.ripples(canvas)
       } yield frames.animateWithCanvasToIO(canvas)).unsafeRunAsync(println _)
    //
    // import doodle.interact.syntax._
    // import cats.implicits._
    // import doodle.svg._
    // (
    //   for {
    //     canvas <- MouseOver.frame.canvas()
    //   } yield MouseOver.frames.animate(canvas)
    // ).unsafeRunAsync(println _)
  }
}
