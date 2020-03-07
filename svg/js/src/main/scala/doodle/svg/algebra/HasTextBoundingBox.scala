package doodle
package svg
package algebra

import doodle.core.BoundingBox
import doodle.core.font.Font
import org.scalajs.dom.svg.Rect

trait HasTextBoundingBox {
  def textBoundingBox(text: String, font: Font): (BoundingBox, Rect)
}
