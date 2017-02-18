package doodle
package jvm

import doodle.core._
import doodle.backend.{Configuration, Draw, Interpreter, Save}
import doodle.backend.Formats.Png

object Java2DCanvas extends Draw with Save[Png] {
  implicit val java2DCanvas: Java2DCanvas.type =
    this

  def debug(image: Image) = {
    import doodle.backend._

    val interpreter = (configuration: Configuration) => (image: Image) => {
      val (context, metrics) = configuration
      def drawBoundingBox(bb: BoundingBox): Finalised = {
        import PathElement._

        val origin = Image.circle(5).fillColor(Color.red).lineColor(Color.red)
        val box =
          Image.closedPath(
            List(moveTo(bb.topLeft), lineTo(bb.topRight), lineTo(bb.bottomRight), lineTo(bb.bottomLeft))
          ).noFill.lineColor(Color.red)

        Finalised.finalise(origin on box, context)
      }

      def loop(finalised: Finalised): Finalised = {
        import Finalised._

        finalised match {
          case Empty =>
            Empty

          case o: OpenPath =>
            On(drawBoundingBox(Renderable.boundingBox(o, metrics)), o)

          case c: ClosedPath =>
            On(drawBoundingBox(Renderable.boundingBox(c, metrics)), c)

          case t: Text =>
            On(drawBoundingBox(Renderable.boundingBox(t, metrics)), t)

          case Beside(l, r) =>
            Beside(loop(l), loop(r))

          case Above(t, b) =>
            Above(loop(t), loop(b))

          case On(o, u) =>
            On(loop(o), loop(u))

          case Transform(tx, i) =>
            Transform(tx, loop(i))
        }
      }

      val finalised = Finalised.finalise(image, context)
      Renderable.layout(loop(finalised), metrics)
    }

    new CanvasFrame(interpreter, image)
  }

  def draw(interpreter: Configuration => Interpreter, image: Image): Unit = {
    new CanvasFrame(interpreter, image)
  }

  def save[F <: Png](fileName: String, interpreter: Configuration => Interpreter, image: Image): Unit = {
    import java.io.File
    import java.awt.image.BufferedImage
    import javax.imageio.ImageIO

    val metrics = Java2D.bufferFontMetrics
    val dc = DrawingContext.blackLines
    val renderable = interpreter(dc, metrics)(image)
    val bb = renderable.boundingBox

    val buffer = new BufferedImage(bb.width.ceil.toInt + 40, bb.height.ceil.toInt + 40, BufferedImage.TYPE_INT_ARGB)
    val bufferCenter = Point.cartesian( (bb.width.ceil + 40) / 2, (bb.height.ceil + 40) / 2 )
    val graphics = Java2D.setup(buffer.createGraphics())
    Java2D.draw(graphics, bufferCenter, dc, renderable)

    val file = new File(fileName)
    ImageIO.write(buffer, "png", file);
    }
}
