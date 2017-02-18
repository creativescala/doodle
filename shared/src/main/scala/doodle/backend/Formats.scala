package doodle
package backend

object Formats {
  sealed trait Png
  sealed trait Gif
  sealed trait Svg
  sealed trait Pdf
  sealed trait PdfAndSvg
  sealed trait Screen
}
