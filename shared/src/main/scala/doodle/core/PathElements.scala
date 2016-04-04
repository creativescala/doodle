package doodle.core

sealed trait PathElement
final case class MoveTo(to: Point) extends PathElement
final case class LineTo(to: Point) extends PathElement
final case class BezierCurveTo(cp1: Point, cp2: Point, to: Point) extends PathElement
