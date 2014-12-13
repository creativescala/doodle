package doodle

// import scalaz.syntax.applicative._
// import scalaz.std.option._

case class DrawingContext(
  lineWidth: Option[Double],
  lineColour: Option[Colour],
  lineCap: Option[Line.Cap],
  lineJoin: Option[Line.Join]
) {
  def stroke: Option[Stroke] =
    //(lineWidth |@| lineColour |@| lineCap |@| lineJoin){Stroke.apply _}
    for {
      w <- lineWidth
      c <- lineColour
      p <- lineCap
      j <- lineJoin
    } yield Stroke(w, c, p, j)

  def lineColour(colour: Colour): DrawingContext =
    this.copy(lineColour = Some(colour))

  def lineWidth(width: Double): DrawingContext =
    this.copy(lineWidth = Some(width))
}

object DrawingContext {
  val whiteLines =
    DrawingContext(
      lineWidth = Some(1.0),
      lineColour = Some(RGB(255, 255, 255)),
      lineCap = Some(Line.Cap.Butt),
      lineJoin = Some(Line.Join.Bevel)
    )
  val blackLines =
    DrawingContext(
      lineWidth = Some(1.0),
      lineColour = Some(RGB(0, 0, 0)),
      lineCap = Some(Line.Cap.Butt),
      lineJoin = Some(Line.Join.Bevel)
    )
}
