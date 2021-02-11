package doodle
package svg
package algebra

import doodle.algebra.generic._
import doodle.core.{PathElement, Transform => Tx}

import scala.collection.mutable

trait PathModule { self: Base with SvgModule =>
  trait Path extends GenericPath[SvgResult] {
    object PathApi extends PathApi {
      val b = bundle
      import b.implicits._
      import b.{svgAttrs, svgTags}

      def closedPath(tx: Tx,
                     fill: Option[Fill],
                     stroke: Option[Stroke],
                     elements: List[PathElement]): SvgResult[Unit] = {
        val dAttr = Svg.toSvgPath(elements, Svg.Closed)
        val set = mutable.Set.empty[self.Tag]
        val style = Svg.toStyle(stroke, fill, set)
        val elt = svgTags.path(svgAttrs.transform := Svg.toSvgTransform(tx),
                               svgAttrs.style := style,
                               svgAttrs.d := dAttr)

        (elt, set, ())
      }

      def openPath(tx: Tx,
                   fill: Option[Fill],
                   stroke: Option[Stroke],
                   elements: List[PathElement]): SvgResult[Unit] = {
        val dAttr = Svg.toSvgPath(elements, Svg.Open)
        val set = mutable.Set.empty[self.Tag]
        val style = Svg.toStyle(stroke, fill, set)
        val elt = svgTags.path(svgAttrs.transform := Svg.toSvgTransform(tx),
                               svgAttrs.style := style,
                               svgAttrs.d := dAttr)

        (elt, set, ())
      }
    }
  }
}
