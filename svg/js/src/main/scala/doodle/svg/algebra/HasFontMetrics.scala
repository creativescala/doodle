package doodle
package svg
package algebra

import doodle.core.BoundingBox
import doodle.core.font.Font

trait HasTextBoundingBox {
  def textBoundingBox(text: String, font: Font): BoundingBox
}
