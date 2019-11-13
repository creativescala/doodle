package doodle
package svg

trait JvmBase extends Base {
  type Builder = scalatags.text.Builder
  type FragT = String
  type Output = String
  val bundle = scalatags.Text
}
