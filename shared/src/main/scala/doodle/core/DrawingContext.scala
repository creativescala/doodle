package doodle
package core

import cats.std.option._
import cats.syntax.cartesian._

import doodle.core.font.Font

final case class DrawingContext(
  lineWidth: Option[Double],
  lineColor: Option[Color],
  lineCap: Option[Line.Cap],
  lineJoin: Option[Line.Join],

  fillColor: Option[Color],

  font: Option[Font]
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

  def lineColorTransform(f: Color => Color): DrawingContext =
    this.copy(lineColor = lineColor.map(f))

  def lineWidth(width: Double): DrawingContext =
    this.copy(lineWidth = if(width <= 0) None else Some(width))

  def fillColor(Color: Color): DrawingContext =
    this.copy(fillColor = Some(Color))

  def fillColorTransform(f: Color => Color): DrawingContext =
    this.copy(fillColor = fillColor.map(f))

  def noLine: DrawingContext =
    this.copy(lineWidth = None)

  def noFill: DrawingContext =
    this.copy(fillColor = None)

  def font(font: Font) =
    this.copy(font = Some(font))
}

object DrawingContext {
  val empty =
    DrawingContext(
      lineWidth = None,
      lineColor = None,
      lineCap = None,
      lineJoin = None,
      fillColor = None,
      font = None
    )
  val whiteLines =
    DrawingContext(
      lineWidth = Some(1.0),
      lineColor = Some(Color.white),
      lineCap = Some(Line.Cap.Butt),
      lineJoin = Some(Line.Join.Miter),
      fillColor = None,
      font = Some(Font.defaultSerif)
    )
  val blackLines =
    DrawingContext(
      lineWidth = Some(1.0),
      lineColor = Some(Color.black),
      lineCap = Some(Line.Cap.Butt),
      lineJoin = Some(Line.Join.Miter),
      fillColor = None,
      font = Some(Font.defaultSerif)
    )
}
