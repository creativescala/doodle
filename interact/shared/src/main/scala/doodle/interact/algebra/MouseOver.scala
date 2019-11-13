package doodle
package interact
package algebra

import doodle.algebra.Algebra
import monix.reactive.Observable

/** Algebra for elements that can respond to Mouseover events */
trait MouseOver[F[_]] extends Algebra[F] {

  /** Attaches a mouse over event listener to the given img. The
   * `Observable[Unit]` produces an event every time the mouseOver event
   * fires. */
  def mouseOver[A](img: F[A]): (F[A], Observable[Unit])
}
