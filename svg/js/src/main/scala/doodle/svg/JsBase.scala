package doodle
package svg

import org.scalajs.dom

trait JsBase extends Base {
  type Builder = dom.Element
  type FragT = dom.Node
  type Output = dom.Element
  val bundle = scalatags.JsDom
}
