package doodle

/**
  * A value in the range [0, 1]
  */
final case class Normalised(get: Double) extends AnyVal {
  def +(that: Normalised): Double =
    this.get + that.get

  def -(that: Normalised): Double =
    this.get - that.get

  def max(that: Normalised): Normalised =
    if(this.get > that.get)
      this
    else
      that

  def min(that: Normalised): Normalised =
    if(this.get < that.get)
      this
    else
      that

  def toPercentage: String =
    s"${get * 100}%"

  def toUnsignedByte: UnsignedByte =
    UnsignedByte.clip(Math.round(get * UnsignedByte.MaxValue.get).toInt)

  def toCanvas: String =
    get.toString
}
object Normalised {
  val MinValue = Normalised(0.0)
  val MaxValue = Normalised(1.0)

  def clip(value: Double): Normalised =
    value match {
      case v if value < 0.0 => MinValue
      case v if value > 1.0 => MaxValue
      case v => Normalised(v)
    }
}
