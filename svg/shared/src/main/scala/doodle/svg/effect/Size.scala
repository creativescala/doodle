package doodle
package svg
package effect

sealed abstract class Size extends Product with Serializable
object Size {

  // Algebraic data type members
  final case class FitToPicture(border: Int) extends Size
  final case class FixedSize(width: Double, height: Double) extends Size

  // Smart constructors

  def fitToPicture(border: Int = 20): Size =
    FitToPicture(border)

  def fixedSize(width: Double, height: Double): Size =
    FixedSize(width, height)
}
