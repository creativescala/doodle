package doodle.core

final case class Point(x: Double, y: Double) {
  def +(v: Vec): Point =
    Point(this.x + v.x, this.y + v.y)
}
