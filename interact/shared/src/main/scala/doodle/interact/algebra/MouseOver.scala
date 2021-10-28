package doodle
package interact
package algebra

import doodle.algebra.Algebra
import fs2.Pure
import fs2.Stream

/** Algebra for elements that can respond to Mouseover events */
trait MouseOver[F[_]] extends Algebra[F] {

  /** Attaches a mouse over event listener to the given img. The stream produces
    * an event every time the mouseOver event fires.
    */
  def mouseOver[A](img: F[A]): (F[A], Stream[Pure, Unit])
}
