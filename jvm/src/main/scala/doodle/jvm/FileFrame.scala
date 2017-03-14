package doodle
package jvm

import doodle.core.{DrawingContext,Image,Point}
import doodle.backend._
import doodle.backend.Formats._

import de.erichseifert.vectorgraphics2d.SVGGraphics2D
import de.erichseifert.vectorgraphics2d.PDFGraphics2D

import java.io.{File, FileOutputStream}
import java.awt.{Graphics2D}
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

object FileFrame {
  implicit val pdfSave: Frame.Save[Pdf] =
    makeFileSave[Pdf, PDFGraphics2D, Unit](
      bb => (new PDFGraphics2D(0, 0, bb.width, bb.height, true), ()),
      (fileName, graphics, _) => graphics.writeTo(new FileOutputStream(fileName))
    )

  implicit val svgSave: Frame.Save[Svg] =
    makeFileSave[Svg, SVGGraphics2D, Unit](
      bb => (new SVGGraphics2D(0, 0, bb.width, bb.height), ()),
      (fileName, graphics, _) => graphics.writeTo(new FileOutputStream(fileName))
    )
  implicit val pdfAndSvgSave: Frame.Save[PdfAndSvg] = PdfAndSvgSave
  implicit val pngSave: Frame.Save[Png] =
    makeFileSave[Png, Graphics2D, BufferedImage](
      bb => {
        val buffer = new BufferedImage(bb.width.ceil.toInt + 40, bb.height.ceil.toInt + 40, BufferedImage.TYPE_INT_ARGB)
        val graphics = Java2D.setup(buffer.createGraphics())

        (graphics, buffer)
      },
      (fileName, _, buffer) => {
        val file = new File(fileName)
        ImageIO.write(buffer, "png", file)
      }
    )
  implicit val gifSave: Frame.Save[Gif] =
    makeFileSave[Gif, Graphics2D, BufferedImage](
      bb => {
        val buffer = new BufferedImage(bb.width.ceil.toInt + 40, bb.height.ceil.toInt + 40, BufferedImage.TYPE_INT_ARGB)
        val graphics = Java2D.setup(buffer.createGraphics())

        (graphics, buffer)
      },
      (fileName, _, buffer) => {
        val file = new File(fileName)
        ImageIO.write(buffer, "gif", file)
      }
    )

  def makeFileSave[Format, G <: Graphics2D, A](
    makeGraphics: BoundingBox => (G, A),
    save: (String, G, A) => Unit
  ): Frame.Save[Format] = {
    new Frame.Save[Format] {
      def setup(finaliser: Finaliser, renderer: Renderer): Interpreter.Save[Format] =
        new Interpreter.Save[Format] {
          def interpret(image: Image): String => Unit = {
            val metrics = Java2D.bufferFontMetrics
            val dc = DrawingContext.blackLines
            val configuration = (dc, metrics)
            val finalised = finaliser.run(configuration)(image)
            val bb = finalised.boundingBox
            val pageCenter = Point.cartesian(bb.width / 2, bb.height / 2)
            val (graphics, resource) = makeGraphics(bb)
            val canvas = new Java2DCanvas(graphics, bb.center, pageCenter)
            renderer.run(canvas)(finalised)

            (fileName: String) => {
              save(fileName, graphics, resource)
            }
          }
        }
    }
  }
  object PdfAndSvgSave extends Frame.Save[PdfAndSvg] {
    def setup(finaliser: Finaliser, renderer: Renderer): Interpreter.Save[PdfAndSvg] = {
      new Interpreter.Save[PdfAndSvg] {
        def interpret(image: Image): String => Unit = {
          val svg = svgSave.setup(finaliser, renderer).interpret(image)
          val pdf = pdfSave.setup(finaliser, renderer).interpret(image)

          (fileName: String) => {
            val pdfFileName = fileName + ".pdf"
            val svgFileName = fileName + ".svg"
            svg(svgFileName)
            pdf(pdfFileName)
          }
        }
      }
    }
  }
}
