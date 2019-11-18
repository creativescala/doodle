package doodle
package java2d
package algebra
package reified

import cats.data.WriterT
import doodle.algebra.generic._
import doodle.core.{Transform => Tx}
import doodle.core.font.Font
import java.awt.Graphics2D

trait ReifiedText extends GenericText[Reification] {
  def gc: Graphics2D

  val TextApi = new TextApi {
    def text(tx: Tx, font: Font, text: String): Reification[Unit] =
      WriterT.tell(
        List(
          Reified.text(
            tx,
            text,
            font,
            Java2D.textBounds(gc, text, font)
          )
        )
      )

    def textBoundingBox(text: String, font: Font): BoundingBox =
      Java2D.textBoundingBox(gc, text, font)
  }
}
