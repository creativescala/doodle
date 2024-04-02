import doodle.core._
import doodle.svg._
import doodle.syntax.all._
import scala.scalajs.js.annotation._
import cats.effect.unsafe.implicits.global

@JSExportTopLevel("ParametricSpiral")
object ParametricSpiral {

  def parametricSpiral(angle: Angle): Point =
    Point((Math.exp(angle.toTurns) - 1) * 200, angle)

  def drawCurve(
      points: Int,
      marker: Point => Picture[Unit],
      curve: Angle => Point
  ): Picture[Unit] = {
    // Angle.one is one complete turn. I.e. 360 degrees
    val turn = Angle.one / points.toDouble
    def loop(count: Int): Picture[Unit] = {
      count match {
        case 0 =>
          val pt = curve(Angle.zero)
          marker(pt).at(pt)
        case n =>
          val pt = curve(turn * count.toDouble)
          marker(pt).at(pt).on(loop(n - 1))
      }
    }

    loop(points)
  }

  @JSExport
  def draw(id: String): Unit = {
    val marker = (point: Point) =>
      Picture
        .circle(point.r * 0.125 + 7)
        .fillColor(Color.red.spin(point.angle / 4.0))
        .noStroke

    drawCurve(20, marker, parametricSpiral _).drawWithFrame(Frame(id))
  }
}
