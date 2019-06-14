package doodle
package svg
package examples

import scala.scalajs.js.annotation._
import doodle.syntax._
import doodle.svg._

// Wrapper to run an example in a web page
object Example {
  val frame = Frame("canvas").size(600,600)

  @JSExportTopLevel("Main")
  def main(): Unit = {
    // Squares.squares.draw(frame)
    //
    // SierpinskiRipple.image.draw(frame)
    //
    // GradientCircle.image.draw(frame)
    //
    import doodle.interact.syntax._
    import cats.implicits._
    import scala.concurrent.duration._
    (
      for {
        canvas <- frame.canvas()
      } yield Orbit.frames.delayOnNext(10.milliseconds).animate(canvas)
    ).unsafeRunAsync(println _)
    //
    // import doodle.svg._
    // (
    //   for {
    //     canvas <- MouseOver.frame.canvas()
    //   } yield MouseOver.frames.animate(canvas)
    // ).unsafeRunAsync(println _)
  }
}
