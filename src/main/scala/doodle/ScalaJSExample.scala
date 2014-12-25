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

    val red = Colour.rgb(255, 0, 0)
    val gold = Colour.rgb(255, 255, 0)
    val green = Colour.rgb(0, 255, 0)

    val bauble = Circle(7) lineColour(red) fillColour(red)
    val goldBauble = Circle(10) lineColour(gold) fillColour(gold)

    val treeElement = Triangle(40, 40)

    def row(elements: Int): Image =
      elements match {
        case 1 => bauble on treeElement
        case n => bauble on treeElement beside row(n - 1)
      }

    def tree(levels: Int): Image =
      levels match {
        case 1 => goldBauble above treeElement
        case n => row(n) below tree(n - 1)
      }

    val picture = tree(4) lineColour(green) fillColour(green) above Rectangle(20,40)


    Draw(picture, canvas)
  }
}
 
