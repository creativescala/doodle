package doodle.core

// import scalaz.syntax.applicative._
// import scalaz.std.option._

case class DrawingContext(
  lineWidth: Option[Double],
  lineColour: Option[Colour],
  lineCap: Option[Line.Cap],
  lineJoin: Option[Line.Join],

  fillColour: Option[Colour]
) {
  def stroke: Option[Stroke] =
    //Scalaz applicative syntax apparently doesn't yet compiled in Scala.js
    //(lineWidth |@| lineColour |@| lineCap |@| lineJoin){Stroke.apply _}
    for {
      w <- lineWidth
      c <- lineColour
      p <- lineCap
      j <- lineJoin
    } yield Stroke(w, c, p, j)

  def fill: Option[Fill] =
    fillColour.map(Fill.apply _)

  // A lens library would help to reduce this redundancy in the
  // DrawingContext transformations. However, in the introductory
  // context we're developing this code I don't want to add the
  // complication. At least this serves to motivate lenses!

  def lineColour(colour: Colour): DrawingContext =
    this.copy(lineColour = Some(colour))

  def lineWidth(width: Double): DrawingContext =
    this.copy(lineWidth = Some(width))

  def fillColour(Colour: Colour): DrawingContext =
    this.copy(fillColour = Some(Colour))
}

object DrawingContext {
  val whiteLines =
    DrawingContext(
      lineWidth = Some(1.0),
      lineColour = Some(Colour.rgb(255, 255, 255)),
      lineCap = Some(Line.Cap.Butt),
      lineJoin = Some(Line.Join.Bevel),
      fillColour = None
    )
  val blackLines =
    DrawingContext(
      lineWidth = Some(1.0),
      lineColour = Some(Colour.rgb(0, 0, 0)),
      lineCap = Some(Line.Cap.Butt),
      lineJoin = Some(Line.Join.Bevel),
      fillColour = None
    )
}
