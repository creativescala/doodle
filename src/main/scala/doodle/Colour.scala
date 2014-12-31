package doodle

sealed trait Colour {

  /** Adjust hue by the given angle */
  def spin(angle: Angle) = {
    val original = this.toHSLA
    original.copy(h = original.h + angle)
  }

  /** True if this is approximately equal to that */
  def ~=(that: Colour): Boolean =
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
        s"hsla($h, ${s.toPercentage}, ${l.toPercentage}, ${a.toCanvas})"
    }

  def toHSLA: HSLA = 
    this match {
      case RGBA(r, g, b, a) =>
        val rNormalised = r.get / 255.0
        val gNormalised = g.get / 255.0
        val bNormalised = b.get / 255.0
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
        val hue = Angle.degrees(unnormalisedHue).toDegrees

        val lightness = (cMax + cMin) / 2

        val saturation =
          if(delta == 0.0)
            0.0
          else
            (delta / (1 - Math.abs(2 * lightness - 1)))

        Colour.hsla(hue, saturation, lightness, a.get)

      case HSLA(h, s, l, a) => HSLA(h, s, l, a)
    }

  def toRGBA: RGBA =
    this match {
      case RGBA(r, g, b, a) => RGBA(r, g, b, a)
      case HSLA(h, s, l, a) =>
        s.get match {
          case 0 =>
            val lightness = (l.get * 255).toInt
            Colour.rgba(lightness, lightness, lightness, a.get)
          case s =>
            def hueToRgb(p: Double, q: Double, t: Normalised): Double = {
              t.get match {
                case t if t < 1.0/6.0 => p + (q - p) * 6 * t
                case t if t < 0.5 => q
                case t if t < 2.0/3.0 => p + (q - p) * (2/3 - t) * 6
                case t => p
              }
            }

            val lightness = l.get
            val q =
              if(lightness < 0.5)
                lightness * (1 + s)
              else
                lightness + s - (lightness * s)
            val p = 2 * lightness - q
            val r = hueToRgb(p, q, (h + Angle.degrees(120)).toTurn)
            val g = hueToRgb(p, q, h.toTurn)
            val b = hueToRgb(p, q, (h - Angle.degrees(120)).toTurn)

            Colour.rgba(
              Math.round(r * 255).toInt,
              Math.round(g * 255).toInt,
              Math.round(b * 255).toInt,
              a.get)
        }
    }
}
final case class RGBA(r: UnsignedByte, g: UnsignedByte, b: UnsignedByte, a: Normalised) extends Colour 
final case class HSLA(h: Angle, s: Normalised, l: Normalised, a: Normalised) extends Colour 

object Colour {
  // Convenience constructors that clip their input
  def rgba(r: Int, g: Int, b: Int, a: Double): RGBA =
    RGBA(
      UnsignedByte.clip(r),
      UnsignedByte.clip(g),
      UnsignedByte.clip(b),
      Normalised.clip(a)
    )
  def hsla(h: Double, s: Double, l: Double, a: Double): HSLA =
    HSLA(
      Angle.degrees(h),
      Normalised.clip(s),
      Normalised.clip(l),
      Normalised.clip(a)
    )
  def rgb(r: Int, g: Int, b: Int): Colour =
    rgba(r, g, b, 1.0)
  def hsl(h: Double, s: Double, l: Double) =
    hsla(h, s, l, 1.0)
}
