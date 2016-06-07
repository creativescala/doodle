package doodle
package jvm

import doodle.core.{DrawingContext,Image,Point}
import doodle.backend._
import doodle.backend.Formats._

import de.erichseifert.vectorgraphics2d.SVGGraphics2D
import de.erichseifert.vectorgraphics2d.PDFGraphics2D

import java.io.FileOutputStream

object FileCanvas {
  implicit val pdfCanvas: Save[Pdf] = PdfCanvas
  implicit val svgCanvas: Save[Svg] = SvgCanvas
  implicit val pdfAndSvgCanvas: Save[PdfAndSvg] = PdfAndSvgCanvas

  object PdfCanvas extends Save[Pdf] {
    def save[F <: Pdf](fileName: String, interpreter: Configuration => Interpreter, image: Image): Unit = {
      val metrics = Java2D.bufferFontMetrics
      val dc = DrawingContext.blackLines
      val renderable = interpreter(dc, metrics)(image)
      val bb = renderable.boundingBox
      val pageCenter = Point.cartesian(bb.width / 2, bb.height / 2)
      val graphics = new PDFGraphics2D(0, 0, bb.width, bb.height, true)

      Java2D.draw(graphics, pageCenter, dc, renderable)
      graphics.writeTo(new FileOutputStream(fileName))
    }
  }

  object SvgCanvas extends Save[Svg] {
    def save[F <: Svg](fileName: String, interpreter: Configuration => Interpreter, image: Image): Unit = {
      val metrics = Java2D.bufferFontMetrics
      val dc = DrawingContext.blackLines
      val renderable = interpreter(dc, metrics)(image)
      val bb = renderable.boundingBox
      val pageCenter = Point.cartesian(bb.width / 2, bb.height / 2)
      val graphics = new SVGGraphics2D(0, 0, bb.width, bb.height)

      Java2D.draw(graphics, pageCenter, dc, renderable)
      graphics.writeTo(new FileOutputStream(fileName))
    }
  }

  object PdfAndSvgCanvas extends Save[PdfAndSvg] {
    def save[F <: PdfAndSvg](fileName: String, interpreter: Configuration => Interpreter, image: Image): Unit = {
      val pdfFileName = fileName + ".pdf"
      val svgFileName = fileName + ".svg"

      PdfCanvas.save[Pdf](pdfFileName, interpreter, image)
      SvgCanvas.save[Svg](svgFileName, interpreter, image)
    }
  }
}
