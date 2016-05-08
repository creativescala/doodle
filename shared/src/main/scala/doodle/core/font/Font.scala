package doodle
package core
package font

sealed abstract class FontFamily extends Product with Serializable
final case object Serif extends FontFamily
final case object SansSerif extends FontFamily
final case object Monospaced extends FontFamily
final case class Physical(name: String) extends FontFamily

sealed abstract class FontFace extends Product with Serializable
final case object Bold extends FontFace
final case object Italic extends FontFace
final case object Normal extends FontFace

sealed abstract class FontSize extends Product with Serializable
final case class Points(get: Int) extends FontSize


final case class Font(family: FontFamily, face: FontFace, size: FontSize)
object Font {
  val defaultSerif = Font(Serif, Normal, Points(12))
  val defaultSansSerif = Font(SansSerif, Normal, Points(12))
}
