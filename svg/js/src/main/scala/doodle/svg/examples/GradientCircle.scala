package doodle
package svg
package examples

object GradientCircle {
  // This serves to test layout. The circle at three o'clock should be red and
  // the gradient should progress anticlockwise
  import cats.instances.all._
  import doodle.core._
  import doodle.syntax._
  import doodle.language.Basic
  import doodle.svg._

  val image =
    Basic.picture[Drawing, Unit] { implicit algebra: Basic[Drawing] =>
      import algebra._

      (0 to 360 by 15)
        .map(
          x =>
            circle(50)
              .fillColor(Color.hsl(x.degrees, 0.7, 0.7))
              .at(Point(200, x.degrees)))
        .toList
        .allOn
    }
}
