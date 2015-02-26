package doodle.core

trait Animation {
  def animate: Animation
  def draw: Image
}

case class StaticAnimation(draw: Image) extends Animation {
  val animate = this
}

object Animation {
  def static(image: Image) =
    StaticAnimation(image)
}
