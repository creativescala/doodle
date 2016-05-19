package doodle.core

import utest._
import doodle.syntax._
import japgolly.nyaya.Prop
import japgolly.nyaya.test.Gen
import japgolly.nyaya.test.PropTest._

object AngleSpec extends TestSuite {
  implicit val ec = utest.ExecutionContext.RunNow

  val genNormalized: Gen[Normalized] =
    Gen.choosedouble(0, 1) map Normalized.clip

  def propIdentity(f: Double => Double, name: String): Prop[Double] =
    Prop.test[Double](
      s"$name is an identity",
      { c =>
        val v1 = c
        val v2 = f(c)
        Math.abs(v1 - v2) < 0.1
      }
    )

  val propAnglesEqualAfterNormalization: Prop[(Normalized, Int)] =
    Prop.test(
      s"Angle the same after normalization",
      { case (n, m) => 
        val normalized = n.get.turns.toTurns
        val unnormalized = (n.get + m).turns.normalize.toTurns
        Math.abs(normalized - unnormalized) < 0.1 
      }
    )

  val tests = TestSuite {
    "Conversions to/from degrees is identity"-{
      Gen.choosedouble(0.0, 360.0) mustSatisfy propIdentity(_.degrees.toDegrees, "degrees")
    }

    "Conversions to/from turns is identity"-{
      Gen.choosedouble(0.0, 1.0) mustSatisfy propIdentity(_.turns.toTurns, "turns")
    }

    "Conversions to/from radians is identity"-{
      Gen.choosedouble(0.0, 1.0) mustSatisfy propIdentity(_.radians.toRadians, "radians")
    }

    "Normalization of angles"-{
      (for {
        n <- genNormalized
        m <- Gen.chooseint(1, 10)
      } yield (n, m)) mustSatisfy propAnglesEqualAfterNormalization
    }
  }
}
