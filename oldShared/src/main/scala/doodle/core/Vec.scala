package doodle
package core

/** A 2D vector. We can't use the name `Vector` as Scala already uses it. */
final case class Vec(x: Double, y: Double) {
  def +(that: Vec): Vec = Vec(this.x + that.x, this.y + that.y)
  def -(that: Vec): Vec = Vec(this.x - that.x, this.y - that.y)

  def unary_- : Vec =
    Vec(-x, -y)

  def *(d: Double): Vec = Vec(x*d, y*d)
  def /(d: Double): Vec = Vec(x/d, y/d)

  def left: Vec  = Vec(-y, x)
  def right: Vec = Vec(y, -x)

  def angle: Angle = Angle.radians(math.atan2(y, x))

  def length: Double = math.sqrt(x*x + y*y)

  def normalize: Vec = {
    val len = length
    if(len == 0) Vec(1, 0) else this / len
  }

  def rotate(by: Angle): Vec =
    Vec.polar(this.length, this.angle + by)

  def dot(that: Vec): Double =
    this.x * that.x + this.y * that.y

  /** Z-component of the cross product of `this` and `that` */
  def cross(that: Vec): Double =
    this.x * that.y - this.y * that.x

  def toPoint: Point =
    Point.cartesian(x, y)
}

object Vec {
  val zero  = Vec(0, 0)
  val unitX = Vec(1, 0)
  val unitY = Vec(0, 1)

  def polar(angle: Angle): Vec =
    polar(1.0, angle)

  def polar(r: Double, angle: Angle): Vec =
    Vec(r * angle.cos, r * angle.sin)
}
