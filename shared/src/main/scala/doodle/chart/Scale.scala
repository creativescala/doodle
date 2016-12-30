package doodle
package chart

object Scale {
  val linear: Numerical = Linear
  val logarithmic: Numerical = Logarithmic
  val categorical: Categorical = Categories

  sealed abstract class Numerical extends Product with Serializable
  final case object Linear extends Numerical
  final case object Logarithmic extends Numerical

  sealed abstract class Categorical extends Product with Serializable
  final case object Categories extends Categorical
}
