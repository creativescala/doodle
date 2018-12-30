package doodle
package backend

import scala.annotation.tailrec

sealed abstract class Trampoline[A] {
  def value: A = Trampoline.loop(this)
}
object Trampoline {
  final case class Continue[A](f: () => Trampoline[A]) extends Trampoline[A]
  def continue[A](f: => Trampoline[A]): Trampoline[A] =
    Continue(() => f)

  final case class Stop[A](a: A) extends Trampoline[A]
  def stop[A](a: A): Trampoline[A] = Stop(a)

  @tailrec
  def loop[A](t: Trampoline[A]): A =
    t match {
      case Continue(f) => loop(f())
      case Stop(a) => a
    }
}

