package doodle
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom

@JSExport
object ScalaJSExample {
  @JSExport
  def main(canvas: dom.HTMLCanvasElement): Unit = {
    val ctx = canvas.getContext("2d")
                    .asInstanceOf[dom.CanvasRenderingContext2D]
    val progression =
      Circle(5) beside Rectangle(20,20) beside Circle(15) beside (Circle(20) on Rectangle(40, 40))
    val picture = progression above progression strokeColour (RGB(59, 67, 70)) strokeWidth (5.0)

    Draw(picture, canvas)}
}
