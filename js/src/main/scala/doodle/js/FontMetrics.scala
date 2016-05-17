package doodle
package js

import doodle.core.font._
import doodle.backend.BoundingBox

import org.scalajs.dom

final case class FontMetrics(svg: dom.raw.SVGSVGElement) {
  import scalatags.JsDom.svgTags._
  import scalatags.JsDom.svgAttrs._
  import scalatags.JsDom.tags._
  import scalatags.JsDom.styles._

  def boundingBox(font: Font, characters: String): BoundingBox = {
    val elt = text(
      id:="doodle-font-metrics",
      display:="none",
      font:=FontMetrics.toCss(font),
      characters
    ).render

    val txt = svg.appendChild(elt).asInstanceOf[SVGLocatable]
    val bb = txt.getBBox()
    svg.removeChild(txt)

    BoundingBox(0, 0, bb.width, bb.height)
  }
}
object FontMetrics {
  import FontFamily._
  import FontFace._
  import FontSize._

  def toCss(font: Font): String = {
    val style =
      font.face match {
        case Italic => "italic"
        case _ => "normal"
      }

    val weight =
      font.face match {
        case Bold => "bold"
        case _ => "normal"
      }

    val size =
      font.size match {
        case Points(pts) => s"${pts}pt"
      }

    val family =
      font.family match {
        case Serif => "serif"
        case SansSerif => "sans-serif"
        case Monospaced => "monospaced"
        case Named(name) => name
      }

    s"${style} normal ${weight} ${size} ${family}"
  }
}
