package doodle.svg.algebra

import doodle.svg.JsBase

trait JsTaggedModule extends JsBase {
  trait JsTagged extends Tagged[Tag] {
    self: doodle.algebra.Algebra {
      type Drawing[A] = doodle.algebra.generic.Finalized[SvgResult, A]
    } =>

    import bundle.implicits.stringAttr

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
      tagged(drawing, bundle.tags.a(bundle.attrs.href := href))
  }
}
