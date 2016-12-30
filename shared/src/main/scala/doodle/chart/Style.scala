package doodle
package chart

// Preliminary support for styling charts
final case class Style(
  width: Int,
  height: Int,
  padding: Int
)
object Style {
  val default = Style(600, 600, 10)
}
