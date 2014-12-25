package doodle

/**
  * An angle in degrees, from 0 to 360
  */
final case class Angle(get: Double) extends AnyVal {
  /** Angle as the proportion of a full turn around a circle */
  def toTurn: Normalised =
    Normalised.clip(this.get / 360.0)
}
object Angle {
  def degrees(deg: Double): Angle = {
    def normalise(deg: Double): Double =
      deg match {
        case d if deg < 0.0 =>
          normalise(d + 360.0)
        case d if deg > 360.0 =>
          normalise(d - 360.0)
        case d => d
      }
    Angle(normalise(deg))
  }
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
        val hue =
          unnormalisedHue match {
            case h if unnormalisedHue < 0 =>
              h + 360
            case h if unnormalisedHue > 360 =>
              h - 360
            case h => h
          }

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
            def hueToRgb(p: Double, q: Double, t: Double): Double = {
              val turn = t match {
                case t if t < 0 => t + 1
                case t if t > 1 => t - 1
                case t => t
              }
              turn match {
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
            val r = hueToRgb(p, q, h.toTurn.get + 1.0/3.0)
            val g = hueToRgb(p, q, h.toTurn.get)
            val b = hueToRgb(p, q, h.toTurn.get - 1.0/3.0)

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
