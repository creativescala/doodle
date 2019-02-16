package doodle
package svg
package effect

import org.scalajs.dom

final case class SvgFrame(target: dom.Node)
object SvgFrame {
  def fromId(id: String): Option[SvgFrame] =
    Some(SvgFrame(dom.document.getElementById(id)))
}
