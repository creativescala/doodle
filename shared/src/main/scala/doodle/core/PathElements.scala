package doodle.core

sealed abstract class PathElement extends Product with Serializable
final case class MoveTo(to: Point) extends PathElement
final case class LineTo(to: Point) extends PathElement
final case class BezierCurveTo(cp1: Point, cp2: Point, to: Point) extends PathElement
