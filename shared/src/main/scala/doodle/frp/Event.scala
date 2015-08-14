package doodle
package frp

import scala.collection.mutable

sealed trait Event[A] {
  val listeners: mutable.ListBuffer[Transform[A, _]] = new mutable.ListBuffer()

  def map[B](f: A => B): Event[B] = {
    val node = Transform(f)
    listeners += node
    node
  }

  def foldp[B](seed: B)(f: (A, B) => B): Event[B] = {
    var currentSeed = seed
    val node = Transform { (evt: A) =>
      val nextSeed = f(evt, currentSeed)
      currentSeed = nextSeed
      nextSeed
    }
    listeners += node
    node
  }

  private[frp] def update(in: A): Unit = {
    listeners.foreach(tx => tx.transform(in))
  }
}
final case class Transform[A, B](f: A => B) extends Event[B] {
  private[frp] def transform(in: A): Unit = {
    val transformed = f(in)
    update(transformed)
  }
}
