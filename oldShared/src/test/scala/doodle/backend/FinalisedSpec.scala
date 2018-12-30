package doodle
package backend

import org.scalatest._
import org.scalatest.prop.PropertyChecks
import org.scalacheck.Gen
import doodle.core.{DrawingContext, Image}
import doodle.core.font.Font

class FinalisedSpec extends FlatSpec with Matchers with PropertyChecks {

  "Bounding box of a square" should "tightly enclose the square" in {
    forAll(Gen.choose(-1000, 1000)) { (size: Int) =>
      val length = Math.abs(size)
      val sq = Image.square(size).noLine
      val dummyMetrics = (f: Font, s: String) => BoundingBox.empty
      val finalised =
        Finalised.finalise(sq, DrawingContext.whiteLines, dummyMetrics)

      finalised.boundingBox.width shouldBe length
      finalised.boundingBox.height shouldBe length

      finalised.boundingBox.left shouldBe -(length/2.0)
      finalised.boundingBox.right shouldBe (length/2.0)
      finalised.boundingBox.top shouldBe (length/2.0)
      finalised.boundingBox.bottom shouldBe -(length/2.0)
    }
  }
}
