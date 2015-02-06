package doodle.core

sealed trait PathElement
final case class MoveTo(x: Double, y: Double) extends PathElement
final case class LineTo(x: Double, y: Double) extends PathElement
final case class ArcTo(x1: Double, y1: Double, x2: Double, y2: Double, r: Double) extends PathElement
final case class QuadraticCurveTo(cpx: Double, cpy: Double, x: Double, y: Double) extends PathElement
final case class BezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, x: Double, y: Double) extends PathElement
