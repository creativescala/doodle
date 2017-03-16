package doodle
package js

import org.scalatest._
import org.scalatest.prop.PropertyChecks
import doodle.core.Color
import doodle.syntax.angle._
import doodle.syntax.normalized._
import doodle.syntax.uByte._

class ColorSpec extends FlatSpec with Matchers with PropertyChecks {
  "Colors" should "print to canvas correctly" in {
    val hsla = Color.hsl(120.degrees, 0.5.normalized, 0.5.normalized).toCanvas
    hsla should ===("hsla(120, 50%, 50%, 1)")
    val rgba = Color.rgb(240.uByte, 12.uByte, 12.uByte).toCanvas
    rgba should ===("rgba(240, 12, 12, 1)")
  }
}
