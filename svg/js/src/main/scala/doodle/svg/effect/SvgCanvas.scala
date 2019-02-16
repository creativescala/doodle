package doodle
package svg
package effect

import org.scalajs.dom

final case class SvgCanvas(target: dom.Node){
  def setSvg(svg: dom.Node): Unit = {
    val _ = target.appendChild(svg)
    ()
  }
}
