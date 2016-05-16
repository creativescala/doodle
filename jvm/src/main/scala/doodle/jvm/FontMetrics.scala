package doodle
package jvm

import java.awt.{Font => JFont}
import java.awt.font.{FontRenderContext, TextLayout}

import doodle.core.font._
import doodle.backend.BoundingBox

final case class FontMetrics(context: FontRenderContext) {
  def boundingBox(font: Font, characters: String): BoundingBox = {
    val jFont = FontMetrics.toJFont(font)
    // This bounding box has its origin at the top left corner of the text. We
    // move it so it is in the center of the text, in keeping with the rest of
    // Doodle's builtins.
    val jBox = new TextLayout(characters, jFont, context).getBounds
    val bb = BoundingBox(jBox.getMinX, jBox.getMaxY, jBox.getMaxX, jBox.getMinY)
    BoundingBox(- bb.width/2, bb.height/2, bb.width/2, - bb.height/2)
  }
}
object FontMetrics {
  import FontFamily._
  import FontFace._
  import FontSize._

  def toJFont(font: Font): JFont =
    font match {
      case Font(family, face, size) =>
        val jFamily =
          family match {
            case Serif => JFont.SERIF
            case SansSerif => JFont.SANS_SERIF
            case Monospaced => JFont.MONOSPACED
            case Named(name) => name
          }

        val jStyle =
          face match {
            case Bold => JFont.BOLD
            case Italic => JFont.ITALIC
            case Normal => JFont.PLAIN
          }

        val jSize =
          size match {
            case Points(pts) => pts
          }

        new JFont(jFamily, jStyle, jSize)
    }
}
