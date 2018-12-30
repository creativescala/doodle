package doodle

trait Distance[A] {
  def distance(a1: A, a2: A): Double
}
object Distance {
  import doodle.core._

  def apply[A](f: (A,A) => Double): Distance[A] =
    new Distance[A] {
      def distance(a1: A, a2: A): Double =
        f(a1,a2)
    }

  implicit val angleDistance: Distance[Angle] =
    Distance((a1, a2) =>
      Math.abs((a1-a2).toRadians)
    )

  implicit val pointDistance: Distance[Point] =
    Distance((pt1, pt2) =>
      (pt1 - pt2).length
    )
}
