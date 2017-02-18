package doodle
package core
package font

sealed abstract class FontFamily extends Product with Serializable
object FontFamily {
  final case object Serif extends FontFamily
  final case object SansSerif extends FontFamily
  final case object Monospaced extends FontFamily
  final case class Named(get: String) extends FontFamily

  val serif: FontFamily = Serif
  val sansSerif: FontFamily = SansSerif
  val monospaced: FontFamily = Monospaced
  def named(name: String): FontFamily = Named(name)
}

sealed abstract class FontFace extends Product with Serializable
object FontFace {
  final case object Bold extends FontFace
  final case object Italic extends FontFace
  final case object Normal extends FontFace

  val bold: FontFace = Bold
  val italic: FontFace = Italic
  val normal: FontFace = Normal
}

sealed abstract class FontSize extends Product with Serializable
object FontSize {
  final case class Points(get: Int) extends FontSize

  def points(pts: Int): FontSize = Points(pts)
}


final case class Font(family: FontFamily, face: FontFace, size: FontSize)
object Font {
  import FontFamily._
  import FontFace._
  import FontSize._

  val defaultSerif = Font(serif, normal, points(12))
  val defaultSansSerif = Font(sansSerif, normal, points(12))
}
