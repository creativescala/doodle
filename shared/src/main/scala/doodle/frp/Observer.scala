package doodle
package frp

trait Observer[A] {
  def observe(in: Observation[A]): Unit
}
