package doodle

/**
  * An angle in radians, normalised to be in [0, 2pi)
  */
final case class Angle(get: Double) extends AnyVal {
  def +(that: Angle): Angle =
    Angle.radians(this.get + that.get)

  def -(that: Angle): Angle =
    Angle.radians(this.get - that.get)

  /** Angle as the proportion of a full turn around a circle */
  def toTurn: Normalised =
    Normalised.clip(this.get / Angle.TwoPi)
  def toDegrees: Double =
    this.get * Angle.TwoPi
}
object Angle {
  val TwoPi = math.Pi * 2

  def normalise(rad: Double): Double =
    rad match {
      case r if r < 0.0 =>
        normalise(r + TwoPi)
      case r if r > TwoPi =>
        normalise(r - TwoPi)
      case r => r
    }

  def degrees(deg: Double): Angle = 
    Angle(normalise(deg * TwoPi / 360.0))

  def radians(rad: Double): Angle =
    Angle(normalise(rad))

  /**
    *  A turn represents angle as a proportion of a full turn around a
    *  circle, with a full turn being 1.0
    */
  def turn(t: Double): Angle =
    Angle(normalise(t * TwoPi))
}

/**
  * A value in the range [0, 1]
  */
final case class Normalised(get: Double) extends AnyVal {
  def toPercentage: String =
    s"${get * 100}%"
  def toCanvas: String =
    get.toString
}
object Normalised {
  def clip(value: Double): Normalised =
    value match {
      case v if value < 0.0 => Normalised(0.0)
      case v if value > 1.0 => Normalised(1.0)
      case v => Normalised(v)
    }
}

final case class UnsignedByte(value: Byte) extends AnyVal {
  def toCanvas: String =
    (value + 128).toString
  def get: Int =
    (value + 128)
}
object UnsignedByte {
  def clip(value: Int): UnsignedByte =
    value match {
      case v if value < 0 => UnsignedByte(-128.toByte)
      case v if value > 255 => UnsignedByte(127.toByte)
      case v => UnsignedByte((v - 128).toByte)
    }
}

sealed trait Colour {
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
