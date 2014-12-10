package doodle

sealed trait Colour {
  def toCanvas: String =
    this match {
      case RGB(r, g, b) => s"rgb($r, $g, $b)"
    }
}
final case class RGB(r: Int, g: Int, b: Int) extends Colour

final case class StrokeStyle(line: Line, colour: Colour)

final case class Line(width: Double, cap: Line.Cap = Line.Cap.Butt, join: Line.Join = Line.Join.Miter) 
object Line {
  sealed trait Cap {
    def toCanvas: String =
      this match {
        case Cap.Butt => "butt"
        case Cap.Round => "round"
        case Cap.Square => "square"
      }
  }
  object Cap {
    final case object Butt extends Cap
    final case object Round extends Cap
    final case object Square extends Cap
  }

  sealed trait Join {
    def toCanvas: String =
      this match {
        case Join.Bevel => "bevel"
        case Join.Round => "round"
        case Join.Miter => "miter"
      }
  }
  object Join {
    final case object Bevel extends Join
    final case object Round extends Join
    final case object Miter extends Join
  }
}

sealed trait FillStyle
final case class Solid(colour: Colour) extends FillStyle
