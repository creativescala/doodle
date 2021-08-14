package doodle
package java2d
package algebra
package reified

import cats.data.WriterT
import doodle.algebra.generic._
import doodle.core.BoundingBox
import doodle.core.font.Font
import doodle.core.{Transform => Tx}

import java.awt.Graphics2D
import java.awt.geom.Rectangle2D

trait ReifiedText extends GenericText[Reification] {
  def gc: Graphics2D

  val TextApi = new TextApi {
    type Bounds = Rectangle2D

    def text(
        tx: Tx,
        fill: Option[Fill],
        stroke: Option[Stroke],
        font: Font,
        text: String,
        bounds: Bounds
    ): Reification[Unit] =
      WriterT.tell(
        List(
          Reified.text(
            tx,
            fill,
            stroke,
            text,
            font,
            bounds
          )
        )
      )

    def textBoundingBox(text: String, font: Font): (BoundingBox, Bounds) = {
      val bounds = Java2D.textBounds(gc, text, font)

      (BoundingBox.centered(bounds.getWidth(), bounds.getHeight()), bounds)
    }
  }
}
