package doodle
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom

@JSExport
object ScalaJSExample extends JSApp {
  @JSExport
  def main(): Unit = {
    val canvas: dom.HTMLCanvasElement = dom.document.getElementById("canvas").asInstanceOf[dom.HTMLCanvasElement]
    val ctx = canvas.getContext("2d")
                    .asInstanceOf[dom.CanvasRenderingContext2D]

    def tree(levels: Int): Image =
      levels match {
        case n if n > 1 =>
          val row: Seq[Image] = (1 to n).map(_ => Triangle(40,40))
          row.reduce(_ beside _) below tree(n-1)
        case _ => Triangle(40, 40)
      }

    val picture = tree(4) strokeColour(RGB(0, 255,0)) above Rectangle(20,40)


    Draw(picture, canvas)
  }
}
 
