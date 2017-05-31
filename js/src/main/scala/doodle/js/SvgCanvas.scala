package doodle
package js

import doodle.core._
import doodle.core.transform.Transform
import doodle.backend.{BoundingBox, Canvas}

import org.scalajs.dom

final class SvgCanvas(root: dom.svg.G, center: Point, screenCenter: Point)
    extends Canvas {
  import scalatags.JsDom.styles.{font => cssFont}
  import scalatags.JsDom.{svgTags => svg}
  import scalatags.JsDom.svgAttrs
  import scalatags.JsDom.implicits._

  var transforms: List[String] = List.empty

  def pushTransform(tx: Transform): Unit = {
    val svg = Svg.toSvgTransform(tx)
    transforms = svg :: transforms
  }

  def popTransform(): Unit = {
    transforms match {
      case hd :: tl =>
        transforms = tl
      case Nil =>
        // Do nothing
        ()
    }
  }

  def currentTransform =
    svgAttrs.transform:=(transforms.reverse.mkString)

  def closedPath(context: DrawingContext, elements: List[PathElement]): Unit = {
    val dAttr = Svg.toSvgPath(elements) ++ "Z"
    val style = Svg.toStyle(context)
    val elt = svg.path(currentTransform, svgAttrs.style:=style, svgAttrs.d:=dAttr).render
    root.appendChild(elt)
  }

  def openPath(context: DrawingContext, elements: List[PathElement]): Unit = {
    val dAttr = Svg.toSvgPath(elements)
    val style = Svg.toStyle(context)
    val elt = svg.path(currentTransform, svgAttrs.style:=style, svgAttrs.d:=dAttr).render
    root.appendChild(elt)
  }

  def text(context: DrawingContext,
           tx: Transform,
           boundingBox: BoundingBox,
           characters: String): Unit = {
    val font = context.font.foreach { f =>
      val style = Svg.toStyle(context)
      // SVG x and y coordinates give the bottom left corner of the text. Our
      // bounding box origin is at the center of the text.
      val bottomLeft = Transform.translate(-boundingBox.width/2, -boundingBox.height/2)
      val fullTx = Transform.horizontalReflection andThen tx andThen bottomLeft
      val svgTx = transforms.reverse.mkString ++ " " ++ Svg.toSvgTransform(fullTx)
      val font = FontMetrics.toCss(f)
      val elt = svg.text(svgAttrs.style:=style,
                         svgAttrs.x:=0,
                         svgAttrs.y:=0,
                         svgAttrs.transform:=svgTx,
                         cssFont:=font,
                         characters).render
      root.appendChild(elt)
    }
  }
}
