package doodle
package backend

sealed trait Key
object Key {
  final case object Control extends Key
  final case object Alt extends Key
  final case object Shift extends Key
  final case object Up extends Key
  final case object Down extends Key
  final case object Left extends Key
  final case object Right extends Key
  final case class Character(get: Char) extends Key
  final case object Unknown extends Key
}
