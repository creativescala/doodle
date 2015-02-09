package doodle.core

import doodle.syntax.angle._

sealed trait Color {

  /** Adjust hue by the given angle */
  def spin(angle: Angle) = {
    val original = this.toHSLA
    original.copy(h = original.h + angle)
  }

  /** Lighten the color by the given amount. This is an absolute
    * amount, not an amount relative to the Color's current
    * lightness. Lightness is clipped at Normalised.MaxValue */
  def lighten(lightness: Normalised) = {
    val original = this.toHSLA
    original.copy(l = Normalised.clip(original.l + lightness))
  }

  /** Darken the color by the given amount. This is an absolute
    * amount, not an amount relative to the Color's current
    * lightness. Lightness is clipped at Normalised.MaxValue */
  def darken(darkness: Normalised) = {
    val original = this.toHSLA
    original.copy(l = Normalised.clip(original.l - darkness))
  }

  /** Saturate the color by the given amount. This is an absolute
    * amount, not an amount relative to the Color's current
    * saturation. Saturation is clipped at Normalised.MaxValue */
  def saturate(saturation: Normalised) = {
    val original = this.toHSLA
    original.copy(s = Normalised.clip(original.s + saturation))
  }

  /** Desaturate the color by the given amount. This is an absolute
    * amount, not an amount relative to the Color's current
    * saturation. Saturation is clipped at Normalised.MaxValue */
  def desaturate(desaturation: Normalised) = {
    val original = this.toHSLA
    original.copy(s = Normalised.clip(original.s - desaturation))
  }

  /** Increase the alpha channel by the given amount. */
  def fadeIn(opacity: Normalised) = {
    val original = this.toHSLA
    original.copy(a = Normalised.clip(original.a + opacity))
  }

  /** Decrease the alpha channel by the given amount. */
  def fadeOut(opacity: Normalised) = {
    val original = this.toHSLA
    original.copy(a = Normalised.clip(original.a - opacity))
  }

  def alpha: Normalised =
    this match {
      case RGBA(_, _, _, a) => a
      case HSLA(_, _, _, a) => a
    }

  /** True if this is approximately equal to that */
  def ~=(that: Color): Boolean =
    (this.toRGBA, that.toRGBA) match {
      case (RGBA(r1, g1, b1, a1), RGBA(r2, g2, b2, a2)) =>
        Math.abs(r1 - r2) < 2 &&
        Math.abs(g1 - g2) < 2 &&
        Math.abs(b1 - b2) < 2 &&
        Math.abs(a1 - a2) < 0.1
    }

  def toCanvas: String =
    this match {
      case RGBA(r, g, b, a) =>
        s"rgba(${r.toCanvas}, ${g.toCanvas}, ${b.toCanvas}, ${a.toCanvas})"
      case HSLA(h, s, l, a) =>
        s"hsla(${h.toCanvas}, ${s.toPercentage}, ${l.toPercentage}, ${a.toCanvas})"
    }

  def toHSLA: HSLA =
    this match {
      case RGBA(r, g, b, a) =>
        val rNormalised = r.toNormalised
        val gNormalised = g.toNormalised
        val bNormalised = b.toNormalised
        val cMax = rNormalised max gNormalised max bNormalised
        val cMin = rNormalised min gNormalised min bNormalised
        val delta = cMax - cMin

        val unnormalisedHue =
          if(cMax == rNormalised)
            60 * (((gNormalised - bNormalised) / delta))
          else if(cMax == gNormalised)
            60 * (((bNormalised - rNormalised) / delta) + 2)
          else
            60 * (((rNormalised - gNormalised) / delta) + 4)
        val hue = unnormalisedHue.degrees

        val lightness = Normalised.clip((cMax + cMin) / 2)

        val saturation =
          if(delta == 0.0)
            Normalised.MinValue
          else
            Normalised.clip(delta / (1 - Math.abs(2 * lightness.get - 1)))

        HSLA(hue, saturation, lightness, a)

      case HSLA(h, s, l, a) => HSLA(h, s, l, a)
    }

  def toRGBA: RGBA =
    this match {
      case RGBA(r, g, b, a) => RGBA(r, g, b, a)
      case HSLA(h, s, l, a) =>
        s.get match {
          case 0 =>
            val lightness = l.toUnsignedByte
            RGBA(lightness, lightness, lightness, a)
          case s =>
            def hueToRgb(p: Double, q: Double, t: Normalised): Normalised = {
              Normalised.clip(t.get match {
                case t if t < 1.0/6.0 => p + (q - p) * 6 * t
                case t if t < 0.5 => q
                case t if t < 2.0/3.0 => p + (q - p) * (2/3 - t) * 6
                case t => p
              })
            }

            val lightness = l.get
            val q =
              if(lightness < 0.5)
                lightness * (1 + s)
              else
                lightness + s - (lightness * s)
            val p = 2 * lightness - q
            val r = hueToRgb(p, q, (h + 120.degrees).toTurns)
            val g = hueToRgb(p, q, h.toTurns)
            val b = hueToRgb(p, q, (h - 120.degrees).toTurns)

            RGBA(r.toUnsignedByte, g.toUnsignedByte, b.toUnsignedByte, a)
        }
    }
}
final case class RGBA(r: UnsignedByte, g: UnsignedByte, b: UnsignedByte, a: Normalised) extends Color
final case class HSLA(h: Angle, s: Normalised, l: Normalised, a: Normalised) extends Color

object Color extends CommonColors {
  /** Convenience constructors that clips its input. */
  def rgba(r: Int, g: Int, b: Int, a: Double): RGBA =
    RGBA(
      UnsignedByte.clip(r),
      UnsignedByte.clip(g),
      UnsignedByte.clip(b),
      Normalised.clip(a)
    )

  /** Convenience constructors that clips its input. */
  def hsla(h: Double, s: Double, l: Double, a: Double): HSLA =
    HSLA(
      Angle.degrees(h),
      Normalised.clip(s),
      Normalised.clip(l),
      Normalised.clip(a)
    )

  /** Convenience constructors that clips its input. */
  def rgb(r: Int, g: Int, b: Int): Color =
    rgba(r, g, b, 1.0)

  /** Convenience constructors that clips its input. */
  def hsl(h: Double, s: Double, l: Double) =
    hsla(h, s, l, 1.0)
}
