/*
 * Copyright 2015-2020 Noel Welsh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package doodle
package interact
package animation

import cats._
import cats.implicits._
import doodle.algebra.{Algebra, Picture}
import doodle.effect.Renderer
import doodle.interact.algebra.Redraw
import doodle.interact.effect.AnimationRenderer
import doodle.interact.syntax.animationRenderer._
import monix.execution.Scheduler
import monix.reactive.Observable
import scala.annotation.tailrec

/**
  * A Transducer represents an animation that starts in some initial state and
  * proceeds through various states until it stops. For example, a square might
  * move from an x position of 0 to an x position of 100 in increments of 10. The
  * states then become 0, 10, 20, ..., 100.
  *
  * More abstractly, a transducer is a finite state machine with an additional
  * set of output values, so that each state is associated with some output
  * value. Continuing the example of the moving square, the states are the x
  * position and the output is the square at the given position (for some fixed y
  * coordinate).
  *
  * Transducers should be treated like half open intervals, which means they
  * should generate the inital state but avoid generating a stopping state when
  * possible (for example, when combined in sequence with another transducer).
  *
  * Transducers have several type classes instances:
  *  - Traverse
  *  - Applicative
  *  - Monoid, corresponding to sequential composition (++)
  *
  * The majority of the API is provided by the Cats methods defined on these
  * type classes, so import cats.implicits._ to get a richer API (e.g. toList,
  * filter, etc.)
  *
  * There is another possible monoid, which corresponds to parallel composition
  * (and), if there is a monoid on the type A. Taken with ++ this makes
  * Transducers a Rig or Semiring (depending on how one defines these terms;
  * they are not always defined in the same way).
  */
trait Transducer[Output] { self =>

  /**
    * The type of the state used by this transducer. A type parameter as this
    * isn't really needed outside of the transducer.
    */
  type State

  /**
    * The initial state for this Transducer.
    */
  def initial: State

  /**
    * A method that constructs the next state given the current state. If the
    * current state is a stopped state this method should always return that
    * state.
    */
  def next(current: State): State

  /**
    * A method that returns the output of the current state. If the transducer
    * has stopped it may not have any output, in which case it can throw a
    * java.util.NoSuchElementException. As a result, clients should avoid
    * calling this method when the transducer is in a stopped state. If possible
    * this method should return some other sensible result, rather than throwing
    * an exception, if given a stopped state.
    */
  def output(state: State): Output

  /**
    * True if this state is a stopped (or halt) state, meaning the transducer
    * will never transition to a different state.
    */
  def stopped(state: State): Boolean

  /**
    * Transform the output of this transducer using the given function
    */
  def map[B](f: Output => B): Transducer[B] =
    new Transducer[B] {
      type State = self.State

      val initial: State = self.initial
      def next(current: State): State =
        self.next(current)
      def output(state: State): B =
        f(self.output(state))
      def stopped(state: State): Boolean =
        self.stopped(state)
    }

  /**
    * Append that transducer to this transducer, so that tranducer runs when this
    * one has finished. Both transducers must produce output of the same type.
    */
  def ++(that: Transducer[Output]): Transducer[Output] =
    new Transducer[Output] {
      type State = Either[self.State, that.State]

      val initial: State =
        if (self.stopped(self.initial)) that.initial.asRight
        else self.initial.asLeft

      def next(current: State): State =
        current match {
          case Left(a) =>
            val nextA = self.next(a)
            if (self.stopped(nextA)) that.initial.asRight
            else nextA.asLeft
          case Right(b) => that.next(b).asRight
        }

      def output(state: State): Output =
        state match {
          case Left(a)  => self.output(a)
          case Right(b) => that.output(b)
        }

      def stopped(state: State): Boolean =
        state match {
          case Left(_)  => false
          case Right(b) => that.stopped(b)
        }
    }

  /**
    * When this transducer's next state would be a stopped state, transition to
    * the tranducer created by calling the given function with the current
    * output. If this transducer immediately stops, and hence has no output, there
    * will be no output to pass to the function and therefore the next transducer
    * will not be created.
    *
    * This is like append (++) but allows the final output to determine the
    * transducer that is appended.
    */
  def andThen(f: Output => Transducer[Output]): Transducer[Output] = {
    import Transducer._

    new Transducer[Output] {
      type State = (Boolean, Box[_, Output])

      val initial = (true, Box[self.State, Output](self)(self.initial))

      def next(current: State): State = {
        current match {
          case (first, box: Box[s, Output]) =>
            val nextS = box.next
            if (first && box.transducer.stopped(nextS)) {
              val nextT = f(box.output)
              (false, Box[nextT.State, Output](nextT)(nextT.initial))
            } else {
              (first, Box(box.transducer)(nextS))
            }
        }
      }

      def output(state: (Boolean, Transducer.Box[_, Output])): Output =
        state match {
          case (_, box: Box[s, Output]) => box.output
        }

      def stopped(state: (Boolean, Transducer.Box[_, Output])): Boolean =
        state match {
          case (_, box: Box[s, Output]) => box.stopped
        }
    }
  }

  /**
    * Create a transducer that runs this transducer in parallel with that
    * transducer, stopping when both have stopped. Both transducers must produce
    * output of the same type, and there must be a monoid instance for the
    * output type.
    *
    * If one transducer stops before the other then its last output before
    * stopping is returned as its output until the other transducer stops. If it
    * stops before generating output (i.e. its initial state is a stopping
    * state) than the zero / identity of the monoid is used as its output. This
    * behaviour is usually what we want for animations, and it makes and a
    * monoid instance for transducer with empty as the identity. To stop when
    * either have stopped see [[product]].
    */
  def and(
      that: Transducer[Output]
  )(implicit m: Monoid[Output]): Transducer[Output] =
    new Transducer[Output] {
      type State = (self.State, Output, that.State, Output)

      val initial: State = (
        self.initial,
        if (self.stopped(self.initial)) m.empty else self.output(self.initial),
        that.initial,
        if (that.stopped(that.initial)) m.empty else that.output(that.initial)
      )

      def next(current: State): State = {
        val (a, ao, b, bo) = current
        val nextA = self.next(a)
        val nextB = that.next(b)

        val nextAO = if (self.stopped(nextA)) ao else self.output(nextA)
        val nextBO = if (that.stopped(nextB)) bo else that.output(nextB)

        (nextA, nextAO, nextB, nextBO)
      }

      def output(state: State): Output = {
        val (_, ao, _, bo) = state
        ao |+| bo
      }

      def stopped(state: State): Boolean = {
        val (a, _, b, _) = state
        self.stopped(a) && that.stopped(b)
      }
    }

  /**
    * Create a transducer that runs this transducer in parallel with that
    * transducer, stopping when either has stopped. To stop when both have
    * stopped see [[and]].
    */
  def product[B](that: Transducer[B]): Transducer[(Output, B)] =
    new Transducer[(Output, B)] {
      type State = (self.State, that.State)

      val initial: State = (self.initial, that.initial)

      def next(current: State): State = {
        val (a, b) = current
        (self.next(a), that.next(b))
      }

      def output(state: State): (Output, B) = {
        val (a, b) = state
        (self.output(a), that.output(b))
      }

      def stopped(state: State): Boolean = {
        val (a, b) = state
        self.stopped(a) || that.stopped(b)
      }
    }

  /**
    * Create a transducer that outputs the cumulative results of applying the
    * function f to the output of the underlying transducer. If the underlying
    * transducer has stopped the zero value is produced as the only output.
    */
  def scanLeft[B](zero: B)(f: (B, Output) => B): Transducer[B] =
    new Transducer[B] {
      // State consists of
      // - state of the underlying transducer,
      //
      // - the current cumulative result,
      //
      // - flag indicating if the transducer has stopped (which happens one step
      //   after the underlying transducer stops)
      type State = (self.State, B, Boolean)

      val initial: State = (self.initial, zero, false)

      def next(current: State): State = {
        val (a, b, flag) = current
        if (flag) current
        else if (self.stopped(a)) (a, b, true)
        else (self.next(a), f(b, self.output(a)), false)
      }

      def output(state: State): B = {
        val (_, b, _) = state
        b
      }

      def stopped(state: State): Boolean = {
        val (_, _, flag) = state
        flag
      }
    }

  def foldLeft[B](zero: B)(f: (B, Output) => B): B = {
    @tailrec def loop(b: B, state: State): B =
      if (self.stopped(state)) b
      else loop(f(b, self.output(state)), self.next(state))

    loop(zero, this.initial)
  }

  def foldRight[B](zero: Eval[B])(f: (Output, Eval[B]) => Eval[B]): Eval[B] = {
    def loop(state: State): Eval[B] =
      if (self.stopped(state)) zero
      else f(self.output(state), loop(self.next(state)))

    loop(self.initial)
  }

  def traverse[G[_], B](
      f: Output => G[B]
  )(implicit G: Applicative[G]): G[Transducer[B]] =
    self
      .foldRight[G[Transducer[B]]](Always(G.pure(Transducer.empty))) {
        (output, gtb) =>
          G.map2Eval(f(output), gtb)(Transducer.pure(_) ++ _)
      }
      .value

  /**
    * Construct a transducer by appending this transducer to itself the given
    * number of times.
    *
    * The count must be 0 or greater.
    */
  def repeat(count: Int): Transducer[Output] = {
    assert(
      count >= 0,
      s"A transducer must be repeated 0 or more times. Given $count as the number of repeats."
    )
    0.until(count).foldLeft(Transducer.empty[Output])((t, _) => t.++(this))
  }

  def repeatForever: Transducer[Output] =
    new Transducer[Output] {
      type State = self.State

      def initial: State = self.initial

      def next(current: State): State = {
        val next = self.next(current)
        if (self.stopped(next)) self.initial
        else next
      }

      def output(state: State): Output =
        self.output(state)

      def stopped(state: State): Boolean = false
    }

  /**
    * Convert this transducer to an monix.reactive.Observable
    */
  def toObservable: Observable[Output] =
    Observable.unfold(self.initial) { state =>
      if (self.stopped(state)) None
      else Some((self.output(state), self.next(state)))
    }

  /**
    * Convenience method to animate a transducer.
    */
  def animate[Alg[x[_]] <: Algebra[x], F[_], Frame, Canvas](frame: Frame)(
      implicit
      a: AnimationRenderer[Canvas],
      e: Renderer[Alg, F, Frame, Canvas],
      r: Redraw[Canvas],
      s: Scheduler,
      ev: Output <:< Picture[Alg, F, Unit]
  ): Unit =
    this.toObservable.map(ev(_)).animateFrames(frame)
}
object Transducer {

  /**
    * Aux instance for Transducer. Use when you need to make the State type
    * visible as a type parameter.
    */
  type Aux[S0, O0] = Transducer[O0] { type State = S0 }

  /**
    * This associates a transducer with its state, useful to get around issues
    * with inference of existential types.
    */
  final case class Box[S, O](transducer: Transducer.Aux[S, O])(state: S) {
    self =>
    def next: S =
      transducer.next(state)

    def output: O =
      transducer.output(state)

    def stopped: Boolean =
      transducer.stopped(state)
  }

  implicit val transducerTraverseAndApplicative
    : Traverse[Transducer] with Applicative[Transducer] =
    new Traverse[Transducer] with Applicative[Transducer] {
      def foldLeft[A, B](fa: Transducer[A], b: B)(f: (B, A) => B): B =
        fa.foldLeft(b)(f)

      def foldRight[A, B](fa: Transducer[A], lb: Eval[B])(
          f: (A, Eval[B]) => Eval[B]
      ): Eval[B] =
        fa.foldRight(lb)(f)

      def traverse[G[_], A, B](
          fa: Transducer[A]
      )(f: (A) => G[B])(implicit arg0: Applicative[G]): G[Transducer[B]] =
        fa.traverse(f)(arg0)

      def ap[A, B](ff: Transducer[A => B])(fa: Transducer[A]): Transducer[B] =
        ff.product(fa).map { case (f, a) => f(a) }

      def pure[A](x: A): Transducer[A] =
        Transducer.pure(x)

      override def map[A, B](fa: Transducer[A])(f: A => B): Transducer[B] =
        fa.map(f)
    }

  implicit def transducerMonoid[A]: Monoid[Transducer[A]] =
    new Monoid[Transducer[A]] {
      def combine(x: Transducer[A], y: Transducer[A]): Transducer[A] =
        x.++(y)

      def empty: Transducer[A] =
        Transducer.empty[A]
    }

  def empty[A]: Transducer[A] =
    apply()

  def pure[A](elt: A): Transducer[A] =
    apply(elt)

  def fromList[A](elts: List[A]): Transducer[A] =
    apply(elts: _*)

  def apply[A](elts: A*): Transducer[A] =
    new Transducer[A] {
      type State = Seq[A]

      val initial: State = elts

      def next(current: State): State =
        current match {
          case Seq()  => current
          case _ +: t => t
        }

      def output(state: State): A =
        state match {
          case Seq() =>
            throw new NoSuchElementException(
              "This transducer has no more output."
            )
          case h +: _ => h
        }

      def stopped(state: State): Boolean =
        state match {
          case Seq() => true
          case _     => false
        }
    }
}
