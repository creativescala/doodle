
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom

@JSExport object Main extends JSApp {
  @JSExport def main(): Unit = {
    doodle.canvas.Draw(doodle.Example.picture, "canvas")
  }
}
