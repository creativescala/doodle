package doodle
package svg
package effect

import doodle.core.Color
import org.scalajs.dom

final case class Canvas(target: dom.Node,
                        size: Size,
                        background: Option[Color]) {

  def setSvg(svg: dom.Node): Unit = {
    val _ = target.appendChild(svg)
    ()
  }
}
object Canvas {
  def fromFrame(frame: Frame): Canvas = {
    val target = dom.document.getElementById(frame.id)
    Canvas(target, frame.size, frame.background)
  }
}
