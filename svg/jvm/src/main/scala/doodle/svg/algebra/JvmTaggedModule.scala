package doodle.svg.algebra

import doodle.svg.JvmBase

trait JvmTaggedModule extends JvmBase {
  trait JvmTagged extends Tagged[Tag] {
    self: doodle.algebra.Algebra {
      type Drawing[A] = doodle.algebra.generic.Finalized[SvgResult, A]
    } =>

    import bundle.implicits.stringAttr
    import bundle.tags

    /** Wrap the given Tag around the given Drawing. */
    def tagged[A](drawing: Drawing[A], tag: Tag): Drawing[A] =
      drawing.map((bb, rdr) =>
        val newRdr = rdr.map { case (svg, other, a) =>
          (
            tag.apply(svg.asInstanceOf[bundle.Modifier]).asInstanceOf[Tag],
            other,
            a
          )
        }
        (bb, newRdr)
      )

    /** A utility to include wrap a link (an a tag) around a Drawing. */
    def link[A](drawing: Drawing[A], href: String): Drawing[A] =
      tagged(drawing, tags.a(bundle.attrs.href := href))
  }
}

/*
 * 
+      override def link(
+          bits: Renderable[SvgResult, Unit],
+          href: String
+      ): SvgResult[Unit] = {
         val svg = bits.runA(Tx.identity).value
         (tags.a(svg._1, attrs.href := href), svg._2, ())
       }


 * trait Text extends GenericText[SvgResult] {
    self: HasTextBoundingBox[Rectangle2D] with Algebra {
      type Drawing[A] = Finalized[SvgResult, A]
    } =>
    val TextApi = new TextApi {
      type Bounds = Rectangle2D

      def textBoundingBox(
          text: String,
          font: Font
      ): (BoundingBox, Rectangle2D) =
        self.textBoundingBox(text, font)

      def text(
          tx: Transform,
          fill: Option[Fill],
          stroke: Option[Stroke],
          font: Font,
          text: String,
          bounds: Rectangle2D
      ): SvgResult[Unit] = {
        import bundle.implicits.{Tag as _, *}
        val set = mutable.Set.empty[Tag]
        // (0,0) of the Rectangle2D is the left baseline. For Doodle (0,0) is the
        // center of the bounding box.
        val style = Svg.toStyle(stroke, fill, set)
        val elt = Svg.textTag(text, font, style)(
          bundle.svgAttrs.transform := Svg.toSvgTransform(
            Transform.verticalReflection.andThen(tx)
          ),
          bundle.svgAttrs.x := -(bounds.getMinX() + bounds.getWidth()) / 2.0,
          bundle.svgAttrs.y := (bounds.getMinY() + bounds.getHeight()) / 2.0,
          bundle.svgAttrs.dominantBaseline := "middle"
        )

        (elt, set, ())
      }
    }
  }*/
