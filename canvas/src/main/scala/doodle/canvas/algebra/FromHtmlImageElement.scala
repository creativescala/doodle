package doodle.canvas.algebra

import cats.data.State
import org.scalajs.dom
import doodle.algebra.generic.Finalized
import doodle.core.BoundingBox
import doodle.core.Transform as Tx

/** Algebra indicating a Picture can be created from an HTMLImageElement.
  */
trait FromHtmlImageElement extends doodle.algebra.Algebra {
  type Drawing[A] = Finalized[CanvasDrawing, A]

  /** Create a picture from an HTMLImageElement.
    */
  def fromHtmlImageElement(image: dom.HTMLImageElement): Drawing[Unit] =
    Finalized.leaf { dc =>
      val w = image.width
      val h = image.height
      val bb = BoundingBox.centered(image.width, image.height)
      (
        bb,
        State.inspect(tx =>
          CanvasDrawing.setTransform(
            Tx.verticalReflection.andThen(tx)
          ) >> CanvasDrawing(canvas =>
            canvas.drawImage(image, -w / 2, -h / 2, w, h)
          )
        )
      )
    }
}
