package doodle
package jvm

import doodle.core.{DrawingContext,Image,Point}
import doodle.backend._
import doodle.backend.Formats.Pdf

import de.erichseifert.vectorgraphics2d.{Document,PDFGraphics2D,Processor}
import de.erichseifert.vectorgraphics2d.intermediate.commands.Command
import de.erichseifert.vectorgraphics2d.intermediate.filters.{AbsoluteToRelativeTransformsFilter,FillPaintedShapeAsImageFilter,StateChangeGroupingFilter}
import de.erichseifert.vectorgraphics2d.util.PageSize

import java.io.{FileOutputStream,OutputStream}

object PdfCanvas extends Save[Pdf] {
  implicit val pdfCanvas: PdfCanvas.type =
    this

  def save[F <: Pdf](fileName: String, interpreter: Configuration => Interpreter, image: Image): Unit = {
    val metrics = Java2D.bufferFontMetrics
    val dc = DrawingContext.blackLines
    val renderable = interpreter(dc, metrics)(image)
    val bb = renderable.boundingBox
    val pageCenter = Point.cartesian(bb.width / 2, bb.height / 2)
    val graphics = new CompressedPdfGraphics2D(0, 0, bb.width, bb.height)

    Java2D.draw(graphics, pageCenter, dc, renderable)
    graphics.writeTo(new FileOutputStream(fileName))
  }

  class CompressedPdfGraphics2D(x: Double, y: Double, width: Double, height: Double) extends PDFGraphics2D(x, y, width, height) {
    override def writeTo(out: OutputStream): Unit = {
		  val doc = new CompressedPdfProcessor().process(getCommands(), getPageSize())
		  doc.write(out);
	  }
  }

  class CompressedPdfProcessor() extends Processor {
    override def process(commands: java.lang.Iterable[Command[_]], pageSize: PageSize): Document = {
		  val absoluteToRelativeTransformsFilter =
        new AbsoluteToRelativeTransformsFilter(commands)
		  val paintedShapeAsImageFilter =
        new FillPaintedShapeAsImageFilter(absoluteToRelativeTransformsFilter)
		  val filtered = new StateChangeGroupingFilter(paintedShapeAsImageFilter)
		  val doc = new CompressedPdfDocument(pageSize)
      val iterator = commands.iterator
      while(iterator.hasNext()) {
        doc.handle(iterator.next())
      }
		  doc.close()
      doc
    }
  }
}
