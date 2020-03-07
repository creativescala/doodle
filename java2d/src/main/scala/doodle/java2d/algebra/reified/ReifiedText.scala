package doodle
package java2d
package algebra
package reified

import cats.data.WriterT
import doodle.algebra.generic._
import doodle.core.{BoundingBox, Transform => Tx}
import doodle.core.font.Font
import java.awt.Graphics2D
import java.awt.geom.Rectangle2D

trait ReifiedText extends GenericText[Reification] {
  def gc: Graphics2D

  val TextApi = new TextApi {
    type Bounds = Rectangle2D

    def text(
        tx: Tx,
        font: Font,
        text: String,
        bounds: Bounds
    ): Reification[Unit] =
      WriterT.tell(
        List(
          Reified.text(
            tx,
            text,
            font,
            bounds
          )
        )
      )

    def textBoundingBox(text: String, font: Font): (BoundingBox, Bounds) = {
      val bounds = Java2D.textBounds(gc, text, font)

      (BoundingBox.centered(bounds.getWidth(), bounds.getHeight()),
       bounds)
    }
  }
}
