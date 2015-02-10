
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom

@JSExport object Main extends JSApp {
  val image = doodle.examples.ChristmasTree

  @JSExport def main(): Unit =
    doodle.js.draw(image, "canvas")
}
