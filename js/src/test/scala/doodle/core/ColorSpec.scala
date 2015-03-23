package doodle.core

import utest._
import doodle.syntax.angle._
import doodle.syntax.normalized._
import doodle.syntax.uByte._
import japgolly.nyaya.Prop
import japgolly.nyaya.test.Gen
import japgolly.nyaya.test.PropTest._

object ColorSpec extends TestSuite {
  val tests = TestSuite {
    "Known HSLA converts to expected RGBA"-{
      val blueHSLA = Color.hsl(240.degrees, 0.5.normalized, 0.5.normalized).toRGBA
      val blueRGBA = Color.rgb(64.uByte, 64.uByte, 191.uByte)
      assert(blueHSLA ~= blueRGBA)

      val greenHSLA = Color.hsl(120.degrees, 0.5.normalized, 0.5.normalized).toRGBA
      val greenRGBA = Color.rgb(64.uByte, 191.uByte, 64.uByte)
      assert(greenHSLA ~= greenRGBA)

      val redHSLA = Color.hsl(0.degrees, 0.75.normalized, 0.5.normalized).toRGBA
      val redRGBA = Color.rgb(223.uByte, 32.uByte, 32.uByte)
      assert(redHSLA ~= redRGBA)
    }

    "Known RGBA converts to expected HSLA"-{
      val blueHSLA = Color.hsl(240.degrees, 0.5.normalized, 0.5.normalized)
      val blueRGBA = Color.rgb(64.uByte, 64.uByte, 191.uByte).toHSLA
      assert(blueHSLA ~= blueRGBA)

      val greenHSLA = Color.hsl(120.degrees, 0.5.normalized, 0.5.normalized)
      val greenRGBA = Color.rgb(64.uByte, 191.uByte, 64.uByte).toHSLA
      assert(greenHSLA ~= greenRGBA)

      val redHSLA = Color.hsl(0.degrees, 0.75.normalized, 0.5.normalized)
      val redRGBA = Color.rgb(223.uByte, 32.uByte, 32.uByte).toHSLA
      assert(redHSLA ~= redRGBA)
    }

    "HSLA with 0 saturation converts to gray"-{
      val grey1HSLA = Color.hsl(0.degrees, 0.normalized, 0.5.normalized).toRGBA
      val grey1RGBA = Color.rgb(128.uByte, 128.uByte, 128.uByte)
      assert(grey1HSLA ~= grey1RGBA)

      val grey2HSLA = Color.hsl(0.degrees, 0.normalized, 1.0.normalized).toRGBA
      val grey2RGBA = Color.rgb(255.uByte, 255.uByte, 255.uByte)
      assert(grey2HSLA ~= grey2RGBA)
    }

    "HSLA spin transforms correctly"-{
      val original = Color.hsl(120.degrees, 0.5.normalized, 0.5.normalized)
      val spun = original.spin(60.degrees)
      val unspun = original.spin(-60.degrees)

      assert(spun ~= Color.hsl(180.degrees, 0.5.normalized, 0.5.normalized))
      assert(unspun ~= Color.hsl(60.degrees, 0.5.normalized, 0.5.normalized))
    }

    "Fade in/out transforms correctly"-{
      val original = Color.hsla(120.degrees, 0.5.normalized, 0.5.normalized, 0.5.normalized)
      val fadeOut = original fadeOut(0.5.normalized)
      val fadeIn = original fadeIn(0.5.normalized)

      assert(fadeOut.alpha == 0.0.normalized)
      assert(fadeIn.alpha == 1.0.normalized)
    }

    "Colors print to canvas correctly"-{
      val hsla = Color.hsl(120.degrees, 0.5.normalized, 0.5.normalized).toCanvas
      assert(hsla == "hsla(120, 50%, 50%, 1)")
      val rgba = Color.rgb(240.uByte, 12.uByte, 12.uByte).toCanvas
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
