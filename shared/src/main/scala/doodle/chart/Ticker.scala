package doodle
package chart

object Ticker {
  val parsimonious: Numerical = Parsimonious
  val categorical: Categorical = Categories

  sealed abstract class Numerical extends Product with Serializable
  final case object Parsimonious extends Numerical

  sealed abstract class Categorical extends Product with Serializable
  final case object Categories extends Categorical
}
