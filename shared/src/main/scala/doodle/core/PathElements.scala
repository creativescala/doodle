package doodle.core

sealed trait PathElement
final case class MoveTo(to: Vec) extends PathElement
final case class LineTo(to: Vec) extends PathElement
final case class BezierCurveTo(cp1: Vec, cp2: Vec, to: Vec) extends PathElement
