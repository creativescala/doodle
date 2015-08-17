package doodle
package frp

sealed trait Observation[+A] {
  def map[B](f: A => B): Observation[B] =
    this match {
      case Value(a)  => Value(f(a))
      case Failed(e) => Failed(e)
      case Finished  => Finished
    }

  def flatMap[B](f: A => Observation[B]): Observation[B] =
    this match {
      case Value(a)  => f(a)
      case Failed(e) => Failed(e)
      case Finished  => Finished
    }
}
final case class Value[A](in: A) extends Observation[A]
final case class Failed(exn: Throwable) extends Observation[Nothing]
final case object Finished extends Observation[Nothing]
