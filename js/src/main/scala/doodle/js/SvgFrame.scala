package doodle
package js

import doodle.core._
import doodle.core.transform.Transform
import doodle.backend._
import org.scalajs.dom

final class SvgFrame(svgCanvas: dom.svg.SVG,
                     width: Int,
                     height: Int,
                     finaliser: Finaliser,
                     renderer: Renderer)
    extends Interpreter.Draw {
  def interpret(image: Image): Unit = {
    import scalatags.JsDom.{svgTags => svg}
    import scalatags.JsDom.svgAttrs
    import scalatags.JsDom.implicits._

    val dc = DrawingContext.blackLines
    val metrics = FontMetrics(svgCanvas).boundingBox _
    val configuration = (dc, metrics)

    val finalised = finaliser.run(configuration)(image)

    val screenCenter = Point.cartesian(width / 2, height / 2)
    val center = finalised.boundingBox.center

    val canvasToScreen: Transform =
      Transform.translate(-center.x, -center.y)
        .andThen(Transform.horizontalReflection)
        .andThen(Transform.translate(screenCenter.x, screenCenter.y))

    val root =
      svg.g(svgAttrs.transform:=Svg.toSvgTransform(canvasToScreen)).render
    svgCanvas.appendChild(root)

    val canvas = new SvgCanvas(root, center, screenCenter)
    renderer.run(canvas)(finalised)
  }
}
object SvgFrame {
  def fromElementId(id: String, width: Int, height: Int): Frame.Draw =
    new Frame.Draw {
      val elt = dom.document.getElementById(id).asInstanceOf[dom.svg.SVG]
      def setup(finaliser: Finaliser, renderer: Renderer): Interpreter[Formats.Screen,Unit] =
        new SvgFrame(elt, width, height, finaliser, renderer)
    }
}
