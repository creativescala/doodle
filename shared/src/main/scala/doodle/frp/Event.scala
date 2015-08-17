package doodle
package frp

import scala.collection.mutable

/**
  *  A stream of discrete events of type A
  */
sealed trait Event[A] {
  // We could define this in terms of Functor, Foldable, etc., but we're not
  // doing so for a few reasons:
  //
  // - We're using Doodle as an elementary case study and don't want to rely on
  // "advanced" library like Cats
  //
  // - At the moment Cats doesn't compile on Scala.js
  //
  // Both decisions are subject to revision.

  def mapObservation[B](f: Observation[A] => Observation[B]): Event[B]
  def foldpObservation[B](seed: Observation[B])(f: (Observation[A], Observation[B]) => Observation[B]): Event[B]


  // Utility functions

  def map[B](f: A => B): Event[B] =
    mapObservation[B](_.map(f))
  def foldp[B](seed: B)(f: (A, B) => B): Event[B] =
    foldpObservation[B](Value(seed)){ (a, b) =>
      // Strictly speaking, only an Applicative is needed here
      for {
        theA <- a
        theB <- b
      } yield f(theA, theB)
    }

  /** Transform this Event into an Event of numbers starting for `start`, ending
    * at `end`, and incrementing by `step` each time this Event emits an
    * event. */
  def iota(start: Double, end: Double, step: Double = 1.0): Event[Double] =
    this.foldpObservation(Value(start)){ (_, count) =>
      count flatMap { theCount =>
        if(theCount >= end)
          Finished
        else
          Value(theCount + step)
      }
    }
}
object Event {
  def fromCallback[A]: (Event[A], A => Unit) = {
    val source = new Source[A]()
    val callback = (in: A) => source.push(in)

    (source, callback)
  }
}

/**
  * Internal trait that provides implementation of Event methods in terms of a
  * mutable sequence of observers
  */
private[frp] sealed trait EventSource[A] extends Event[A] {
  val observers: mutable.ListBuffer[Observer[A]] =
    new mutable.ListBuffer()

  def update(observation: Observation[A]): Unit =
    observers.foreach(_.observe(observation))

  def mapObservation[B](f: Observation[A] => Observation[B]): Event[B] = {
    val node = new Transform(f)
    observers += node
    node
  }

    def foldpObservation[B](seed: Observation[B])(f: (Observation[A], Observation[B]) => Observation[B]): Event[B] = {
    var currentSeed = seed
      val node = new Transform( (obs: Observation[A]) => {
          val nextSeed = f(obs, currentSeed)
          currentSeed = nextSeed
          nextSeed
        })
    observers += node
    node
  }
}
final class Source[A]() extends Event[A] with EventSource[A] {
  def push(in: A): Unit =
    update(Value(in))
}
final class Transform[A, B](f: Observation[A] => Observation[B]) extends Observer[A] with EventSource[B] {
  def observe(in: Observation[A]): Unit =
    update(f(in))
}
