package doodle
package core

import cats.instances.option._
import cats.syntax.all._

import doodle.core.font.Font

final case class DrawingContext(
  lineWidth: Option[Double],
  lineColor: Option[Color],
  lineCap: Option[Line.Cap],
  lineJoin: Option[Line.Join],

  fill: Option[Fill],

  font: Option[Font]
) {
  def stroke: Option[Stroke] =
    (lineWidth, lineColor, lineCap, lineJoin) mapN { Stroke.apply _ }

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

  def fillColor(color: Color): DrawingContext =
    this.copy(fill = Some(Fill.Color(color)))

  def fillColorTransform(f: Color => Color): DrawingContext = fill match {
    case Some(Fill.Color(c)) => this.copy(fill = Some(Fill.Color(f(c))))
    case _ => this
  }

  def fillGradient(gradient: Gradient): DrawingContext =
    this.copy(fill = Some(Fill.Gradient(gradient)))

  def fillGradientTransform(f: Gradient => Gradient): DrawingContext = fill match {
    case Some(Fill.Gradient(g)) => this.copy(fill = Some(Fill.Gradient(f(g))))
    case _ => this
  }

  def noLine: DrawingContext =
    this.copy(lineWidth = None)

  def noFill: DrawingContext =
    this.copy(fill = None)

  def font(font: Font): DrawingContext =
    this.copy(font = Some(font))
}

object DrawingContext {
  val empty =
    DrawingContext(
      lineWidth = None,
      lineColor = None,
      lineCap = None,
      lineJoin = None,
      fill = None,
      font = None
    )
  val whiteLines =
    DrawingContext(
      lineWidth = Some(1.0),
      lineColor = Some(Color.white),
      lineCap = Some(Line.Cap.Butt),
      lineJoin = Some(Line.Join.Miter),
      fill = None,
      font = Some(Font.defaultSerif)
    )
  val blackLines =
    DrawingContext(
      lineWidth = Some(1.0),
      lineColor = Some(Color.black),
      lineCap = Some(Line.Cap.Butt),
      lineJoin = Some(Line.Join.Miter),
      fill = None,
      font = Some(Font.defaultSerif)
    )
}
