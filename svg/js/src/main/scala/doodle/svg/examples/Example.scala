package doodle
package svg
package examples

import scala.scalajs.js.annotation._
import doodle.core.Color
import doodle.svg._
import doodle.syntax._

// Wrapper to run an example in a web page
@JSExportTopLevel("Main")
object Example {
  val frame =
    Frame("canvas")
      .size(600, 600)
      .background(Color.midnightBlue)

  @JSExport
  def main(): Unit = {
    // Squares.squares.drawWithFrame(frame)
    //
    // SierpinskiRipple.image.drawWithFrame(frame)
    //
    // GradientCircle.image.drawWithFrame(frame)
    //
    // import doodle.interact.syntax._
    // import cats.implicits._

    // Orbit.frames.animateFrames(frame)
    //
    // import cats.instances.all._
    // import doodle.syntax._
    // import doodle.interact.syntax._
    //   (for {
    //      canvas <- frame.canvas
    //      frames <- Ripples.ripples(canvas)
    //      a <- frames.animateWithCanvasToIO(canvas)
    //    } yield a).unsafeRunAsync(println _)
    //
    // import doodle.interact.syntax._
    // import cats.implicits._
    // import doodle.svg._
    // (
    //   for {
    //     canvas <- MouseOver.frame.canvas()
    //   } yield MouseOver.frames.animate(canvas)
    // ).unsafeRunAsync(println _)
    GradientSquares.image.drawWithFrame(frame)
  }
}
