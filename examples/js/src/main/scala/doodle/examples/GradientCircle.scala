package doodle
package examples

object GradientCircle {
  // This serves to test layout. The circle at three o'clock should be red and
  // the gradient should progress anticlockwise
  import cats.instances.all._
  import doodle.core._
  import doodle.syntax._
  import doodle.svg._

  val image: Picture[Unit] =
    (0 to 360 by 15)
      .map(
        x =>
          circle[Algebra, Drawing](50)
            .fillColor(Color.hsl(x.degrees, 0.7, 0.7))
            .at(Point(200, x.degrees)))
      .toList
      .allOn
}
