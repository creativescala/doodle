package doodle.core

import utest._
import doodle.syntax.angle._
import doodle.syntax.normalized._
import japgolly.nyaya.Prop
import japgolly.nyaya.test.Gen
import japgolly.nyaya.test.PropTest._

object ColorSpec extends TestSuite {
  val tests = TestSuite {
    "Known HSLA converts to expected RGBA"-{
      val blueHSLA = Color.hsla(240, 0.5, 0.5, 1.0).toRGBA
      val blueRGBA = Color.rgba(64, 64, 191, 1.0)
      assert(blueHSLA ~= blueRGBA)

      val greenHSLA = Color.hsla(120, 0.5, 0.5, 1.0).toRGBA
      val greenRGBA = Color.rgba(64, 191, 64, 1.0)
      assert(greenHSLA ~= greenRGBA)

      val redHSLA = Color.hsla(0, 0.75, 0.5, 1.0).toRGBA
      val redRGBA = Color.rgba(223, 32, 32, 1.0)
      assert(redHSLA ~= redRGBA)
    }

    "Known RGBA converts to expected HSLA"-{
      val blueHSLA = Color.hsla(240, 0.5, 0.5, 1.0)
      val blueRGBA = Color.rgba(64, 64, 191, 1.0).toHSLA
      assert(blueHSLA ~= blueRGBA)

      val greenHSLA = Color.hsla(120, 0.5, 0.5, 1.0)
      val greenRGBA = Color.rgba(64, 191, 64, 1.0).toHSLA
      assert(greenHSLA ~= greenRGBA)

      val redHSLA = Color.hsla(0, 0.75, 0.5, 1.0)
      val redRGBA = Color.rgba(223, 32, 32, 1.0).toHSLA
      assert(redHSLA ~= redRGBA)
    }

    "HSLA with 0 saturation converts to gray"-{
      val grey1HSLA = Color.hsla(0, 0, 0.5, 1.0).toRGBA
      val grey1RGBA = Color.rgba(128, 128, 128, 1.0)
      assert(grey1HSLA ~= grey1RGBA)

      val grey2HSLA = Color.hsla(0, 0, 1.0, 1.0).toRGBA
      val grey2RGBA = Color.rgba(255, 255, 255, 1.0)
      assert(grey2HSLA ~= grey2RGBA)
    }

    "HSLA spin transforms correctly"-{
      val original = Color.hsla(120, 0.5, 0.5, 1.0)
      val spun = original.spin(60.degrees)
      val unspun = original.spin(-60.degrees)

      assert(spun ~= Color.hsla(180, 0.5, 0.5, 1.0))
      assert(unspun ~= Color.hsla(60, 0.5, 0.5, 1.0))
    }

    "Fade in/out transforms correctly"-{
      val original = Color.hsla(120, 0.5, 0.5, 0.5)
      val fadeOut = original fadeOut(0.5.normalized)
      val fadeIn = original fadeIn(0.5.normalized)

      assert(fadeOut.alpha == 0.0.normalized)
      assert(fadeIn.alpha == 1.0.normalized)
    }

    "Colors print to canvas correctly"-{
      val hsla = Color.hsl(120, 0.5, 0.5).toCanvas
      assert(hsla == "hsla(120, 50%, 50%, 1)")
      val rgba = Color.rgb(240, 12, 12).toCanvas
      assert(rgba == "rgba(240, 12, 12, 1)")
    }

    val genAngle: Gen[Angle] =
      Gen.chooseint(0, 360) map (d => Angle.degrees(d.toDouble))

    val genNormalized: Gen[Normalized] =
      Gen.choosedouble(0, 1) map Normalized.clip

    val genUnsignedByte: Gen[UnsignedByte] =
      Gen.chooseint(0, 255) map (UnsignedByte.clip _)

    val genHSLA: Gen[HSLA] =
      for {
        h <- genAngle
        s <- genNormalized
        l <- genNormalized
        a <- genNormalized
      } yield HSLA(h, s, l, a) 

    val genRGBA: Gen[RGBA] =
      for {
        r <- genUnsignedByte
        g <- genUnsignedByte
        b <- genUnsignedByte
        a <- genNormalized
      } yield RGBA(r, g, b, a)

    def propIdentity[A <: Color](f: A => A, name: String): Prop[A] =
      Prop.test[A](s"$name is an identity", c => f(c) ~= c)

    ".toRGBA andThen .toHSLA is the identity"-{
      genHSLA mustSatisfy propIdentity[HSLA]((c => c.toRGBA.toHSLA), "toRGBA andThen toHSLA")
    }

    ".toHSLA andThen.toRGBA is the identity"-{
      genRGBA mustSatisfy propIdentity[RGBA]((c => c.toHSLA.toRGBA), "toHSLA andThen toRGBA")
    }
  }
}
