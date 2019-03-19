package doodle
package svg
package effect

sealed abstract class Size extends Product with Serializable
object Size {

  // Algebraic data type members
  final case class FitToImage(border: Int) extends Size
  final case class FixedSize(width: Double, height: Double) extends Size

  // Smart constructors

  def fitToImage(border: Int = 20): Size =
    FitToImage(border)

  def fixedSize(width: Double, height: Double): Size =
    FixedSize(width, height)
}
