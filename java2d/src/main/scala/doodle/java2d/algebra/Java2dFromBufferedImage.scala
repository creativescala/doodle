package doodle.java2d.algebra

import cats.Eval
import cats.data.State
import cats.data.WriterT
import doodle.algebra.FromBufferedImage
import doodle.algebra.generic.Finalized
import doodle.core.BoundingBox
import doodle.core.Transform
import doodle.java2d.Drawing
import doodle.java2d.algebra.reified.Reified

import java.awt.image.BufferedImage

trait Java2dFromBufferedImage extends FromBufferedImage {
  self: Algebra { type Drawing[A] = doodle.java2d.Drawing[A] } =>

  def fromBufferedImage(in: BufferedImage): Drawing[Unit] =
    Finalized.leaf { _ =>
      val w = in.getWidth()
      val h = in.getHeight()
      val bb = BoundingBox.centered(w.toDouble, h.toDouble)
      (
        bb,
        State.inspect { (tx: Transform) =>
          WriterT.tell[Eval, List[Reified]](List(Reified.bitmap(tx, in)))
        }
      )
    }
}
