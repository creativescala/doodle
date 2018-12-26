package doodle
package core

sealed trait Gradient extends Product with Serializable
object Gradient {
  final case class Linear(start: Point,
                          end: Point,
                          stops: Seq[(Color, Double)],
                          cycleMethod: CycleMethod)
      extends Gradient
  final case class Radial(outer: Point,
                          inner: Point,
                          radius: Double,
                          stops: Seq[(Color, Double)],
                          cycleMethod: CycleMethod)
      extends Gradient

  def linear(start: Point,
             end: Point,
             stops: Seq[(Color, Double)],
             cycleMethod: CycleMethod): Gradient =
    Linear(start, end, stops, cycleMethod)

  def radial(outer: Point,
             inner: Point,
             radius: Double,
             stops: Seq[(Color, Double)],
             cycleMethod: CycleMethod): Gradient =
    Radial(outer, inner, radius, stops, cycleMethod)

  def dichromaticVertical(color1: Color, color2: Color, length: Double) =
    Linear(Point.zero,
           Point.Cartesian(0, length),
           List((color1, 0.0), (color2, 1.0)),
           CycleMethod.repeat)

  def dichromaticHorizontal(color1: Color, color2: Color, length: Double) =
    Linear(Point.zero,
           Point.Cartesian(length, 0),
           List((color1, 0.0), (color2, 1.0)),
           CycleMethod.repeat)

  def dichromaticRadial(color1: Color, color2: Color, radius: Double) =
    Radial(Point.zero,
           Point.zero,
           radius,
           List((color1, 0.0), (color2, 1.0)),
           CycleMethod.repeat)

  sealed trait CycleMethod extends Product with Serializable {}
  object CycleMethod {
    final case object NoCycle extends CycleMethod
    final case object Reflect extends CycleMethod
    final case object Repeat extends CycleMethod

    val noCycle: CycleMethod = NoCycle
    val reflect: CycleMethod = Reflect
    val repeat: CycleMethod = Repeat
  }
}
