package doodle
package interact
package easing

import monix.reactive.Observable

/**
  * An easing function is a function from [0,1] to the real numbers (but usually
  * numbers in [0,1]) that is used to construct animation that move in a pleasing
  * way.
  *
  * All easing functions must return 0.0 for input 0.0 and 1.0 for input 1.0.
  */
trait Easing extends Function1[Double, Double] {

  /**
    * Construct an easing function that has the first half of its output from
    * this easing function, and the second half from that easing function.
    *
    * For the input [0, 0.5) the resulting function uses this easing function,
    * and for [0.5, 1] uses that easing function. The input to the two easing
    * functions is linearly scaled so that they both receive a value in the
    * range [0, 1]. Their output is scaled in half so the first function
    * generates values in [0, 0.5] and the second in [0.5, 1.0].
    */
  def followedBy(that: Easing): Easing =
    Easing { t =>
      if (t < 0.5) this(t * 2.0) / 2.0
      else (that((t - 0.5) * 2.0) / 2.0) + 0.5
    }

  /**
    * Reflect this easing around x = 0.5 and y = 0.5. Constructs an "out" easing
    * from an "in" easing and vice versa.
    */
  def reflect: Easing =
    Easing { t =>
      1.0 - this(1.0 - t)
    }

  /**
    * Convert to an Observable that produces a total of steps values, starting at 0.0 and
    * finishing at 1.0
    */
  def toObservable(steps: Int): Observable[Double] =
    Observable
      .range(0L, steps.toLong)
      .map(
        step =>
          if (step == 0) 0.0
          else if (step == steps) 1.0
          else this(step.toDouble / steps.toDouble)
      )
}
object Easing {

  /**
    * Construct an easing function from a Double => Double function
    */
  def apply(f: Double => Double): Easing =
    new Easing {
      def apply(in: Double): Double = f(in)
    }

  /**
    * The identity easing function simply returns its input
    */
  val identity: Easing = Easing(t => t)

  /**
    * Linear easing, is equivalent to the identity
    */
  val linear: Easing = identity

  /**
    * The quadratic easing f(t) = t^2
    */
  val quadratic: Easing = Easing(t => t * t)

  /**
    * The cubic easing f(t) = t^3
    */
  val cubic: Easing = Easing(t => t * t * t)

  /**
    * The sin easing f(t) = sin(t * (Math.PI / 2))
    */
  val sin: Easing = Easing(t => Math.sin(t * 0.5 * Math.PI))

  /**
    * The easing function that moves in a circular path
    */
  val circle: Easing = Easing(t => 1 - Math.sqrt(1 - t * t))

  /**
    * Easing function that overshoots its destination and then smoothly comes back
    */
  val back: Easing =
    Easing(t => t * t * ((1.70158 + 1) * t - 1.70158))

  /**
    * The elastic easing function. Has a little bounce. Might not be correct.
    */
  val elastic: Easing = {
    val p = 0.3
    val s = Math.asin(1) * (0.3 / (2 * Math.PI))

    Easing { t =>
      Math.pow(2, 10 * t - 1) * Math.sin((s - t - 1) / (p / (2 * Math.PI)))
    }
  }
}
