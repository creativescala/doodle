package doodle
package core
package transform

/** Representation of an affine transformation as an augmented matrix. */
final case class Transform(elements: Array[Double]) {
  def apply(point: Point): Point = {
    val x = point.x
    val y = point.y

    val newX = (x * elements(0)) + (y * elements(1)) + elements(2)
    val newY = (x * elements(3)) + (y * elements(4)) + elements(5)

    Point(newX, newY)
  }

  def apply(vec: Vec): Vec =
    this.apply(vec.toPoint).toVec

  def andThen(that: Transform): Transform = {
    val a = this.elements
    val b = that.elements
    Transform(
      Array(b(0)*a(0) + b(1)*a(3),  b(0)*a(1) + b(1)*a(4),  b(0)*a(2) + b(1)*a(5) + b(2),
            b(3)*a(0) + b(4)*a(3),  b(3)*a(1) + b(4)*a(4),  b(3)*a(2) + b(4)*a(5) + b(5),
            0,                      0,                      1)
    )
  }
}
object Transform {
  val identity = scale(1.0, 1.0)

  def scale(x: Double, y: Double): Transform =
    Transform(Array(x, 0, 0,
                    0, y, 0,
                    0, 0, 1))

  def rotate(angle: Angle): Transform =
    Transform(Array(angle.cos, -angle.sin, 0,
                    angle.sin,  angle.cos, 0,
                          0,            0, 1))

  def translate(x: Double, y: Double): Transform =
    Transform(Array(1, 0, x,
                    0, 1, y,
                    0, 0, 1))

  def translate(v: Vec): Transform =
    translate(v.x, v.y)

  /** Reflect horizontally (around the X-axis) */
  val horizontalReflection: Transform =
    Transform(Array(1,  0, 0,
                    0, -1, 0,
                    0,  0, 1))

  /** Reflect vertically (around the Y-axis) */
  val verticalReflection: Transform =
    Transform(Array(-1, 0, 0,
                     0, 1, 0,
                     0, 0, 1))
}
