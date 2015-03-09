package doodle.core

/**
 * A vector in 2D. We can't use the name `Vector` as Scala already uses it.
 */
final case class Vec(x: Double, y: Double) {
  def +(that: Vec): Vec = Vec(this.x + that.x, this.y + that.y)
  def -(that: Vec): Vec = Vec(this.x - that.x, this.y - that.y)

  def *(d: Double): Vec = Vec(x*d, y*d)
  def /(d: Double): Vec = Vec(x/d, y/d)
}
