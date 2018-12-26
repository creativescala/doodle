package doodle
package syntax

object approximatelyEqual {
  implicit class ApproximatelyEqualOps[A](a1: A) {
    def ~=(a2: A)(implicit distance: Distance[A]): Boolean =
      distance.distance(a1, a2) < 0.01
  }
}
