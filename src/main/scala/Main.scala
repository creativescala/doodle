import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom

import doodle.core._
import doodle.syntax.angle._
import doodle.syntax.normalised._

@JSExport
object Main extends JSApp {
  @JSExport
  def main(): Unit = {
    val red = Colour.rgb(255, 0, 0)
    val gold = Colour.rgb(255, 255, 0)
    val green = Colour.rgb(0, 255, 0)
    val brown = Colour.rgb(169, 69, 19)
    val darkGreen = green.darken(0.3.clip)

    val bauble = Circle(7) lineColour(red) fillColour(red)
    val goldBauble = Circle(10) lineColour(gold) fillColour(gold)

    val levels = 4

    def treeElement = {
      val colour = green.spin((Math.random() * 30).degrees)
        .darken((Math.random() * 0.1).clip)
        .desaturate((Math.random() * 0.1).clip)
      Triangle(40, 40) lineColour(colour) fillColour(colour)
    }

    def row(elements: Int): Image =
      elements match {
        case 1 => bauble on treeElement
        case n => bauble on treeElement beside row(n - 1)
      }

    def tree(levels: Int): Image =
      levels match {
        case 1 => treeElement
        case n => row(n) below tree(n - 1)
      }

    val treeBackground = Triangle(40 * levels, 40 * levels) fillColour(darkGreen)
    val trunk = Rectangle(20,40) lineColour(brown) fillColour(brown)
    val picture = goldBauble above (tree(levels) on treeBackground) above trunk

    doodle.canvas.Draw(picture, "canvas")
  }
}

