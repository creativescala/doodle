package doodle
package backend

import org.scalatest._
import org.scalatest.prop.PropertyChecks
import org.scalacheck.{Arbitrary, Gen}
import doodle.core.{DrawingContext, Image}
import doodle.core.font.Font

class StackSafetySpec extends FlatSpec with Matchers with PropertyChecks {
  def genImage(size: Int): Gen[Image] =
    if(size == 0)
      Gen.const(Image.empty)
    else
      Gen.oneOf(
        // On
        for {
          c <- genImage(size - 1)
          f <- genImage(size - 1)
        } yield c on f,
        // Above
        for {
          t <- genImage(size - 1)
          b <- genImage(size - 1)
        } yield t above b,
        // Beside
        for {
          l <- genImage(size - 1)
          r <- genImage(size - 1)
        } yield l beside r,
        // A lot of "on" to consume stack space
        Gen.const(
          (1 to 2000).map{ i => Image.empty }.foldLeft(Image.empty){ _ on _ }
        ),
        // A lot of "above" to consume stack space
        Gen.const(
          (1 to 2000).map{ i => Image.empty }.foldLeft(Image.empty){ _ above _ }
        ),
        // A lot of "beside" to consume stack space
        Gen.const(
          (1 to 2000).map{ i => Image.empty }.foldLeft(Image.empty){ _ beside _ }
        ),
        // Leaf
        Gen.const(Image.circle(10))
      )

  implicit val arbitraryImage: Arbitrary[Image] = Arbitrary(genImage(4))

  "Finalising an Image" should "be stack safe" in {
    forAll { (i: Image) =>
      noException should be thrownBy Finalised.finalise(i, DrawingContext.whiteLines)
    }
  }

  "Rendering an Image" should "be stack safe" in {
    val dummyMetrics = (f: Font, s: String) => BoundingBox.empty
    forAll { (i: Image) =>
      val finalised = Finalised.finalise(i, DrawingContext.whiteLines)
      noException should be thrownBy Renderable.layout(finalised, dummyMetrics)
    }
  }
}
