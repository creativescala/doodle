package doodle
package chart

object Axis {
  val numerical: Numerical =
    Numerical(None, Ticker.parsimonious, Scale.linear)

  val categorical: Categorical =
    Categorical(None, Ticker.categorical)

  final case class Numerical(
    label: Option[String],
    ticker: Ticker.Numerical,
    scale: Scale.Numerical
  )

  final case class Categorical(
    label: Option[String],
    ticker: Ticker.Categorical
  )

  sealed abstract class Direction extends Product with Serializable
  final case object Horizontal extends Direction
  final case object Vertical extends Direction
}
