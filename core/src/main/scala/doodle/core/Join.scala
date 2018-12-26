package doodle
package core

sealed trait Join extends Product with Serializable
object Join {
  final case object Bevel extends Join
  final case object Round extends Join
  final case object Miter extends Join

  val bevel: Join = Bevel
  val round: Join = Round
  val miter: Join = Miter
}
