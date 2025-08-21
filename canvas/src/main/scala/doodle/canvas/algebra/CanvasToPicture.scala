package doodle.canvas.algebra

import doodle.algebra.ToPicture
import doodle.canvas.Picture
import org.scalajs.dom

object CanvasToPicture {
  object HTMLImageElementToPicture
      extends ToPicture[dom.HTMLImageElement, CanvasAlgebra] {
    def toPicture(in: dom.HTMLImageElement): Picture[Unit] =
      new Picture[Unit] {
        def apply(implicit algebra: CanvasAlgebra): algebra.Drawing[Unit] =
          algebra.fromHtmlImageElement(in)
      }
  }
}
