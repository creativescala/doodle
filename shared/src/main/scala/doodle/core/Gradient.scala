package doodle.core

sealed trait Gradient extends Product with Serializable {}

object Gradient {
  final case class Linear(start: Point, end: Point, stops: Seq[(Color, Double)], cycleMethod: CycleMethod) extends Gradient
  final case class Radial(outer: Point, inner: Point, radius: Double, stops: Seq[(Color, Double)], cycleMethod: CycleMethod) extends Gradient

  def DichromaticVertical(color1: Color, color2: Color, length: Double) =
    Linear(Point.zero, Point.Cartesian(0, length), List((color1, 0.0), (color2, 1.0)), CycleMethod.Repeat())

  def DichromaticHorizontal(color1: Color, color2: Color, length: Double) =
    Linear(Point.zero, Point.Cartesian(length, 0), List((color1, 0.0), (color2, 1.0)), CycleMethod.Repeat())

  def DichromaticRadial(color1: Color, color2: Color, radius: Double) =
    Radial(Point.zero, Point.zero, radius, List((color1, 0.0), (color2, 1.0)), CycleMethod.Repeat())

  sealed trait CycleMethod extends Product with Serializable {}

  object CycleMethod {
    final case class NoCycle() extends CycleMethod
    final case class Reflect() extends CycleMethod
    final case class Repeat() extends CycleMethod
  }
}