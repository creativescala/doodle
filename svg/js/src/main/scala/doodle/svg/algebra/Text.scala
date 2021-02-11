package doodle
package svg
package algebra

import doodle.algebra.generic._
import doodle.core.{BoundingBox, Transform => Tx}
import doodle.core.font.Font
import scala.collection.mutable
import org.scalajs.dom.svg.Rect
import scalatags.JsDom.svgAttrs

trait TextModule extends JsBase {
  trait Text extends GenericText[SvgResult] { self: HasTextBoundingBox =>
    val TextApi = new TextApi {
      type Bounds = Rect

      def textBoundingBox(text: String, font: Font): (BoundingBox, Rect) =
        self.textBoundingBox(text, font)

      def text(
          tx: Tx,
          fill: Option[Fill],
          stroke: Option[Stroke],
          font: Font,
          text: String,
          bounds: Rect
      ): SvgResult[Unit] = {
        import bundle.implicits.{Tag => _, _}
        val set = mutable.Set.empty[Tag]
        // (0,0) of the Rect is the left baseline. For Doodle (0,0) is the
        // center of the bounding box.
        val style = Svg.toStyle(stroke, fill, set)
        val elt = Svg.textTag(text, font)(
          svgAttrs.x := -(bounds.x + bounds.width) / 2.0,
          svgAttrs.y := (bounds.y + bounds.height) / 2.0,
          svgAttrs.style := style)

        (elt, set, ())
      }
    }
  }
}
