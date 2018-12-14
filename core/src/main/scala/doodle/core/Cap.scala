package doodle
package core

sealed trait Cap extends Product with Serializable
object Cap {
  final case object Butt extends Cap
  final case object Round extends Cap
  final case object Square extends Cap

  val butt: Cap = Butt
  val round: Cap = Round
  val square: Cap = Square
}
