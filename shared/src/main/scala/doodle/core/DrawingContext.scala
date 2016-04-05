package doodle.core

import cats.std.option._
import cats.syntax.cartesian._

final case class DrawingContext(
  lineWidth: Option[Double],
  lineColor: Option[Color],
  lineCap: Option[Line.Cap],
  lineJoin: Option[Line.Join],

  fillColor: Option[Color]
) {
  def stroke: Option[Stroke] =
    (lineWidth |@| lineColor |@| lineCap |@| lineJoin) map { Stroke.apply _ }

  def fill: Option[Fill] =
    fillColor.map(Fill.apply _)

  // A lens library would help to reduce this redundancy in the
  // DrawingContext transformations. However, in the introductory
  // context we're developing this code I don't want to add the
  // complication. At least this serves to motivate lenses!

  def lineColor(color: Color): DrawingContext =
    this.copy(lineColor = Some(color))

  def lineWidth(width: Double): DrawingContext =
    this.copy(lineWidth = if(width <= 0) None else Some(width))

  def fillColor(Color: Color): DrawingContext =
    this.copy(fillColor = Some(Color))

  def noLine: DrawingContext =
    this.copy(lineWidth = None)

  def noFill: DrawingContext =
    this.copy(fillColor = None)
}

object DrawingContext {
  val empty =
    DrawingContext(
      lineWidth = None,
      lineColor = None,
      lineCap = None,
      lineJoin = None,
      fillColor = None
    )
  val whiteLines =
    DrawingContext(
      lineWidth = Some(1.0),
      lineColor = Some(Color.white),
      lineCap = Some(Line.Cap.Butt),
      lineJoin = Some(Line.Join.Miter),
      fillColor = None
    )
  val blackLines =
    DrawingContext(
      lineWidth = Some(1.0),
      lineColor = Some(Color.black),
      lineCap = Some(Line.Cap.Butt),
      lineJoin = Some(Line.Join.Miter),
      fillColor = None
    )
}
