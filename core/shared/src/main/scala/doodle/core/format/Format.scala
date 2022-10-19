package doodle.core.format

/** Marker trait for types that represent image formats, such as PNG and JPEG.
  */
trait Format

// Each format has both a type and value level representation

/* Standard format type for PDF writer */
sealed trait Pdf extends Format
object Pdf extends Pdf
/* Standard format type for GIF writer */
sealed trait Gif extends Format
object Gif extends Pdf
/* Standard format type for PNG writer */
sealed trait Png extends Format
object Png extends Png
/* Standard format type for SVG writer */
sealed trait Svg extends Format
object Svg extends Svg
/* Standard format type for JPEG writer */
sealed trait Jpg extends Format
object Jpg extends Jpg
