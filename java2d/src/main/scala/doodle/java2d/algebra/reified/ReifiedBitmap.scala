package doodle
package java2d
package algebra
package reified

import cats.Eval
import cats.data.{State, WriterT}
import doodle.algebra.generic._
import doodle.core.{BoundingBox, Transform}
import java.io.File
import javax.imageio.ImageIO

trait ReifiedBitmap extends doodle.algebra.Bitmap[Drawing] {
  def read(file: File): Drawing[Unit] = {
    Finalized.leaf{ _ =>
      val bi = ImageIO.read(file)
      val w = bi.getWidth()
      val h = bi.getHeight()
      val bb = BoundingBox.centered(w.toDouble, h.toDouble)
      (bb,
       State.inspect{ (tx: Transform) =>
         WriterT.tell[Eval, List[Reified]](List(Reified.bitmap(tx, bi)))
       })

    }
  }
}
