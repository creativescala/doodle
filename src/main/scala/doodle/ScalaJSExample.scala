package doodle
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom

@JSExport
object ScalaJSExample {
  @JSExport
  def main(canvas: dom.HTMLCanvasElement): Unit = {
    val ctx = canvas.getContext("2d")
                    .asInstanceOf[dom.CanvasRenderingContext2D]
    val picture =
      Circle(5) beside Rectangle(20,20) beside Circle(15) beside (Circle(20) on Rectangle(40, 40))

    Scene(picture above picture, canvas)}
}
