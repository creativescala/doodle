package doodle.core

trait Animation {
  def next: Animation
  def image: Image
}

case class StaticAnimation(image: Image) extends Animation {
  val next = this
}

object Animation {
  def static(image: Image) =
    StaticAnimation(image)
}
