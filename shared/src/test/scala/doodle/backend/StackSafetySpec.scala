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
    val dummyMetrics = (f: Font, s: String) => BoundingBox.empty
    forAll { (i: Image) =>
      noException should be thrownBy Finalised.finalise(i, DrawingContext.whiteLines, dummyMetrics)
    }
  }

  "Rendering an Image" should "be stack safe" in {
    val dummyMetrics = (f: Font, s: String) => BoundingBox.empty
    val dummyCanvas = new Canvas {
      def closedPath(context: doodle.core.DrawingContext,elements: List[doodle.core.PathElement]): Unit = ()
      def openPath(context: doodle.core.DrawingContext,elements: List[doodle.core.PathElement]): Unit = ()
      def text(context: doodle.core.DrawingContext,tx: doodle.core.transform.Transform,boundingBox: doodle.backend.BoundingBox,characters: String): Unit = ()
    }
    forAll { (i: Image) =>
      val finalised = Finalised.finalise(i, DrawingContext.whiteLines, dummyMetrics)
      noException should be thrownBy Render.render(dummyCanvas, finalised)
    }
  }
}
