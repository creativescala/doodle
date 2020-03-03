package doodle
package svg
package algebra

import doodle.algebra.generic.GenericText
import doodle.core.{BoundingBox, Transform => Tx}
import doodle.core.font.Font
import scala.collection.mutable

trait TextModule extends JsBase {
  trait Text extends GenericText[SvgResult] { self: HasTextBoundingBox =>
    val TextApi = new TextApi {
      def textBoundingBox(text: String, font: Font): BoundingBox =
        self.textBoundingBox(text, font)

      def text(tx: Tx, font: Font, text: String): SvgResult[Unit] = {
        val set = mutable.Set.empty[Tag]
        val elt = Svg.textTag(text, font)

        (elt, set, ())
      }
    }
  }
}
