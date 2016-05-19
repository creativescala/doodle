package doodle
package jvm

import doodle.core.{DrawingContext,Image,Point}
import doodle.backend._
import doodle.backend.Formats.Pdf

import de.erichseifert.vectorgraphics2d.PDFGraphics2D

import java.io.FileOutputStream

object PdfCanvas extends Save[Pdf] {
  implicit val pdfCanvas: PdfCanvas.type =
    this

  def save[F <: Pdf](fileName: String, interpreter: Configuration => Interpreter, image: Image): Unit = {
    val metrics = Java2D.bufferFontMetrics
    val dc = DrawingContext.blackLines
    val renderable = interpreter(dc, metrics)(image)
    val bb = renderable.boundingBox
    val pageCenter = Point.cartesian(bb.width / 2, bb.height / 2)
    val graphics = new PDFGraphics2D(0, 0, bb.width, bb.height)

    Java2D.draw(graphics, pageCenter, renderable)
    graphics.writeTo(new FileOutputStream(fileName))
  }
}
