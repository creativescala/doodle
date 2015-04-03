package doodle.core

// import scalaz.syntax.applicative._
// import scalaz.std.option._

case class DrawingContext(
  lineWidth: Option[Double],
  lineColor: Option[Color],
  lineCap: Option[Line.Cap],
  lineJoin: Option[Line.Join],

  fillColor: Option[Color]
) {
  def stroke: Option[Stroke] =
    //Scalaz applicative syntax apparently doesn't yet compiled in Scala.js
    //(lineWidth |@| lineColor |@| lineCap |@| lineJoin){Stroke.apply _}
    for {
      w <- lineWidth
      c <- lineColor
      p <- lineCap
      j <- lineJoin
    } yield Stroke(w, c, p, j)

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
}

object DrawingContext {
  val whiteLines =
    DrawingContext(
      lineWidth = Some(1.0),
      lineColor = Some(Color.white),
      lineCap = Some(Line.Cap.Butt),
      lineJoin = Some(Line.Join.Bevel),
      fillColor = None
    )
  val blackLines =
    DrawingContext(
      lineWidth = Some(1.0),
      lineColor = Some(Color.black),
      lineCap = Some(Line.Cap.Butt),
      lineJoin = Some(Line.Join.Bevel),
      fillColor = None
    )
}
