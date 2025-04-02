/*
 * Copyright 2015 Creative Scala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package doodle
package core

import doodle.core.syntax.all.*

import scala.math.*

/** Represents a color. Internally two representations are used: RGB and Oklch.
  * Other formats are converted to one of these two representations. All colors
  * always have an alpha channel.
  */
sealed abstract class Color extends Product with Serializable {
  import Color.*

  // Accessors ----------------------------------------------

  def red: UnsignedByte =
    this.toRgb.r

  def green: UnsignedByte =
    this.toRgb.g

  def blue: UnsignedByte =
    this.toRgb.b

  def hue: Angle =
    this.toOklch.h

  /** An alias for chroma */
  def saturation: Double =
    chroma

  def chroma: Double =
    this.toOklch.c

  def lightness: Normalized =
    this.toOklch.l

  def alpha: Normalized =
    this match {
      case Rgb(_, _, _, a)   => a
      case Oklch(_, _, _, a) => a
    }

  // Color manipulation ------------------------------------

  /** Copies this color, changing the hue to the given value */
  def hue(angle: Angle): Color =
    this.toOklch.copy(h = angle)

  /** Copies this color, changing the saturation to the given value */
  def saturation(c: Double): Color =
    this.toOklch.copy(c = c)

  /** Copies this color, changing the lightness to the given value */
  def lightness(l: Normalized): Color =
    this.toOklch.copy(l = l)

  /** Copies this color, changing the alpha to the given value */
  def alpha(a: Normalized): Color =
    this match {
      case Rgb(r, g, b, _)   => Rgb(r, g, b, a)
      case Oklch(l, c, h, _) => Oklch(l, c, h, a)
    }

  /** Rotate hue by the given angle */
  def spin(angle: Angle): Color = {
    val original = this.toOklch
    original.copy(h = original.h + angle)
  }

  /** Lighten the color by the given amount. This is an absolute amount, not an
    * amount relative to the Color's current lightness. Lightness is clipped at
    * Normalized.MaxValue
    */
  def lighten(lightness: Normalized): Color = {
    val original = this.toOklch
    original.copy(l = Normalized.clip(original.l + lightness))
  }

  /** Darken the color by the given amount. This is an absolute amount, not an
    * amount relative to the Color's current lightness. Lightness is clipped at
    * Normalized.MaxValue
    */
  def darken(darkness: Normalized): Color = {
    val original = this.toOklch
    original.copy(l = Normalized.clip(original.l - darkness))
  }

  /** Saturate the color by the given amount. This is an absolute amount, not an
    * amount relative to the Color's current saturation.
    */
  def saturate(saturation: Double): Color = {
    val original = this.toOklch
    original.copy(c = original.c + saturation)
  }

  /** Desaturate the color by the given amount. This is an absolute amount, not
    * an amount relative to the Color's current saturation.
    */
  def desaturate(desaturation: Double): Color = {
    val original = this.toOklch
    original.copy(c = original.c - desaturation)
  }

  /** Increase the alpha channel by the given amount. */
  def fadeIn(opacity: Normalized): Color = {
    this match {
      case Rgb(r, g, b, a) =>
        Rgb(r, g, b, Normalized.clip(a + opacity))
      case Oklch(l, c, h, a) =>
        Oklch(l, c, h, Normalized.clip(a + opacity))
    }
  }

  /** Decrease the alpha channel by the given amount. */
  def fadeOut(opacity: Normalized): Color = {
    this match {
      case Rgb(r, g, b, a) =>
        Rgb(r, g, b, Normalized.clip(a - opacity))
      case Oklch(l, c, h, a) =>
        Oklch(l, c, h, Normalized.clip(a - opacity))
    }
  }

  /** Lighten the color by the given *relative* amount. For example, calling
    * `aColor.lightenBy(0.1.normalized)` increases the lightness by 10% of the
    * current lightness.
    */
  def lightenBy(lightness: Normalized): Color = {
    val original = this.toOklch
    original.copy(l = Normalized.clip(original.l.get * (1 + lightness.get)))
  }

  /** Darken the color by the given *relative* amount. For example, calling
    * `aColor.darkenBy(0.1.normalized)` decreases the lightness by 10% of the
    * current lightness.
    */
  def darkenBy(darkness: Normalized) = {
    val original = this.toOklch
    original.copy(l = Normalized.clip(original.l.get * (1 - darkness.get)))
  }

  /** Saturate the color by the given *relative* amount. For example, calling
    * `aColor.saturateBy(0.1)` increases the saturation by 10% of the current
    * saturation.
    */
  def saturateBy(saturation: Double): Color = {
    val original = this.toOklch
    original.copy(c = original.c * (1 + saturation))
  }

  /** Desaturate the color by the given *relative* amount. For example, calling
    * `aColor.desaturateBy(0.1)` decreases the saturation by 10% of the current
    * saturation.
    */
  def desaturateBy(desaturation: Double) = {
    val original = this.toOklch
    original.copy(c = original.c * (1 - desaturation))
  }

  /** Increase the alpha channel by the given relative amount. */
  def fadeInBy(opacity: Normalized) = {
    this match {
      case Rgb(r, g, b, a) =>
        Rgb(r, g, b, Normalized.clip(a.get * (1 + opacity.get)))
      case Oklch(l, c, h, a) =>
        Oklch(l, c, h, Normalized.clip(a.get * (1 + opacity.get)))
    }
  }

  /** Decrease the alpha channel by the given relative amount. */
  def fadeOutBy(opacity: Normalized) = {
    this match {
      case Rgb(r, g, b, a) =>
        Rgb(r, g, b, Normalized.clip(a.get * (1 - opacity.get)))
      case Oklch(l, c, h, a) =>
        Oklch(l, c, h, Normalized.clip(a.get * (1 - opacity.get)))
    }
  }

  // Other -------------------------------------------------

  /** True if this is approximately equal to that */
  def ~=(that: Color): Boolean =
    (this.toRgb, that.toRgb) match {
      case (Rgb(r1, g1, b1, a1), Rgb(r2, g2, b2, a2)) =>
        Math.abs(r1 - r2) < 2 &&
        Math.abs(g1 - g2) < 2 &&
        Math.abs(b1 - b2) < 2 &&
        Math.abs(a1 - a2) < 0.1
    }

  /** Convert to hue, saturation, and lightness components. Might be useful for
    * legacy applications that use HSL instead of OkLCh.
    */
  def toHsl: (Angle, Normalized, Normalized) =
    this.toRgb match {
      case Rgb(r, g, b, a) =>
        val rNormalized = r.toNormalized
        val gNormalized = g.toNormalized
        val bNormalized = b.toNormalized
        val cMax = rNormalized max gNormalized max bNormalized
        val cMin = rNormalized min gNormalized min bNormalized
        val delta = cMax - cMin

        val unnormalizedHue =
          if cMax == rNormalized then
            60 * (((gNormalized - bNormalized) / delta))
          else if cMax == gNormalized then
            60 * (((bNormalized - rNormalized) / delta) + 2)
          else 60 * (((rNormalized - gNormalized) / delta) + 4)
        val hue = unnormalizedHue.degrees

        val lightness = Normalized.clip((cMax + cMin) / 2)

        val saturation =
          if delta == 0.0 then Normalized.MinValue
          else Normalized.clip(delta / (1 - Math.abs(2 * lightness.get - 1)))

        (hue, saturation, lightness)
    }

  def toOklch: Oklch =
    this match {
      case c: Oklch                     => c
      case Rgb(red, green, blue, alpha) =>
        // See https://bottosson.github.io/posts/oklab/
        // for conversions to and from OkLch

        // Convert sRGB to Linear RGB
        def inverseGammaCorrect(c: Double): Double =
          if c <= 0.04045 then c / 12.92 else pow((c + 0.055) / 1.055, 2.4)

        val rLinear = inverseGammaCorrect(red.get / 255.0)
        val gLinear = inverseGammaCorrect(green.get / 255.0)
        val bLinear = inverseGammaCorrect(blue.get / 255.0)

        // Convert Linearear RGB to LMS
        val l =
          0.4122214708f * rLinear + 0.5363325363f * gLinear + 0.0514459929f * bLinear
        val m =
          0.2119034982f * rLinear + 0.6806995451f * gLinear + 0.1073969566f * bLinear
        val s =
          0.0883024619f * rLinear + 0.2817188376f * gLinear + 0.6299787005f * bLinear

        val l_ = cbrt(l)
        val m_ = cbrt(m)
        val s_ = cbrt(s)

        // LMS to OkLab
        val L = 0.2104542553f * l_ + 0.7936177850f * m_ - 0.0040720468f * s_
        val a = 1.9779984951f * l_ - 2.4285922050f * m_ + 0.4505937099f * s_
        val b = 0.0259040371f * l_ + 0.7827717662f * m_ - 0.8086757660f * s_

        // Convert OkLab to OKLCh
        val C = sqrt(a * a + b * b)
        val h = Angle.radians(atan2(b, a))

        Oklch(L.normalized, C, h, alpha)
    }

  def toRgb: Rgb =
    this match {
      case c: Rgb                => c
      case Oklch(l, c, h, alpha) =>
        // See https://bottosson.github.io/posts/oklab/ for the conversion

        // Convert to Lab
        val L = l.get
        val a = c * h.cos
        val b = c * h.sin

        val l_ = L + 0.3963377774f * a + 0.2158037573f * b
        val m_ = L - 0.1055613458f * a - 0.0638541728f * b
        val s_ = L - 0.0894841775f * a - 1.2914855480f * b

        val l3 = l_ * l_ * l_
        val m3 = m_ * m_ * m_
        val s3 = s_ * s_ * s_

        val rLinear =
          +4.0767416621f * l3 - 3.3077115913f * m3 + 0.2309699292f * s3
        val gLinear =
          -1.2684380046f * l3 + 2.6097574011f * m3 - 0.3413193965f * s3
        val bLinear =
          -0.0041960863f * l3 - 0.7034186147f * m3 + 1.7076147010f * s3

        def gammaCorrect(c: Double): Double =
          if c < 0.0031308 then 12.92 * c
          else 1.055 * pow(c, 1.0 / 2.4) - 0.055

        val red = (gammaCorrect(rLinear) * 255).round.toInt.uByte
        val green = (gammaCorrect(gLinear) * 255).round.toInt.uByte
        val blue = (gammaCorrect(bLinear) * 255).round.toInt.uByte
        Rgb(red, green, blue, alpha)
    }
}
object Color extends CommonColors {
  final case class Rgb(
      r: UnsignedByte,
      g: UnsignedByte,
      b: UnsignedByte,
      a: Normalized
  ) extends Color
  final case class Oklch(l: Normalized, c: Double, h: Angle, a: Normalized)
      extends Color

  /** Construct a [[Color]] in terms of hue, saturation, lightness, and alpha
    * components.
    */
  def hsl(h: Angle, s: Double, l: Double, a: Double): Color = {
    s match {
      case 0 =>
        val lightness = l.normalized.toUnsignedByte
        Rgb(lightness, lightness, lightness, a.normalized)
      case s =>
        def hueToRgb(p: Double, q: Double, t: Normalized): Normalized = {
          Normalized.wrap(t.get match {
            case t if t < 1.0 / 6.0 => p + (q - p) * 6 * t
            case t if t < 0.5       => q
            case t if t < 2.0 / 3.0 => p + (q - p) * (2.0 / 3.0 - t) * 6
            case _                  => p
          })
        }

        val lightness = l
        val q =
          if lightness < 0.5 then lightness * (1 + s)
          else lightness + s - (lightness * s)
        val p = 2 * lightness - q
        val r = hueToRgb(p, q, Normalized.wrap((h + 120.degrees).toTurns))
        val g = hueToRgb(p, q, Normalized.wrap(h.toTurns))
        val b = hueToRgb(p, q, Normalized.wrap((h - 120.degrees).toTurns))

        Rgb(r.toUnsignedByte, g.toUnsignedByte, b.toUnsignedByte, a.normalized)
    }
  }

  /** Construct a [[Color]] in terms of hue, saturation, and lightness
    * components. The alpha value defaults to 1.0 (fully opaque).
    */
  def hsl(h: Angle, s: Double, l: Double): Color =
    hsl(h, s, l, 1.0)

  /** Construct a [[Color]] in terms of perceptual lightness, chroma, hue, and
    * alpha.
    */
  def oklch(l: Double, c: Double, h: Angle, a: Double): Color =
    Oklch(l.normalized, c, h, a.normalized)

  /** Construct a [[Color]] in terms of perceptual lightness, chroma, hue. The
    * alpha values defaults to 1.0 (fully opaque).
    */
  def oklch(l: Double, c: Double, h: Angle): Color =
    oklch(l, c, h, 1.0)

  def rgb(
      r: UnsignedByte,
      g: UnsignedByte,
      b: UnsignedByte,
      a: Normalized
  ): Color =
    Rgb(r, g, b, a)

  /** Construct a [[Color]] in terms of red, green, blue, and alpha components.
    */
  def rgb(r: Int, g: Int, b: Int, a: Double): Color =
    rgb(r.uByte, g.uByte, b.uByte, a.normalized)

  /** Construct a [[Color]] in terms of red, green, and blue components. The
    * alpha value defaults to 1.0 (fully opaque).
    */
  def rgb(r: UnsignedByte, g: UnsignedByte, b: UnsignedByte): Color =
    rgb(r, g, b, 1.0.normalized)

  /** Construct a [[Color]] in terms of red, green, and blue components. The
    * alpha value defaults to 1.0 (fully opaque).
    */
  def rgb(r: Int, g: Int, b: Int): Color =
    rgb(r, g, b, 1.0)

  /** Parse a hexadecimal `String`, as commonly used to specify colors on the
    * web, into a `Color`. The following formats are supported:
    *
    *   - RGB
    *   - #RGB
    *   - Rgb
    *   - #Rgb
    *   - RRGGBB
    *   - #RRGGBB
    *   - RRGGBBAA
    *   - #RRGGBBAA
    *
    * Throws `java.lang.IllegalArgumentException` if the input is not a
    * supported format.
    */
  def fromHex(hex: String): Color = {
    def error(): Nothing =
      throw new IllegalArgumentException(
        s"The string $hex doesn't represent a CSS hex-color"
      )

    def parseHex(offset: Int): Int =
      hex(offset) match {
        case '0'   => 0
        case '1'   => 1
        case '2'   => 2
        case '3'   => 3
        case '4'   => 4
        case '5'   => 5
        case '6'   => 6
        case '7'   => 7
        case '8'   => 8
        case '9'   => 9
        case 'a'   => 10
        case 'A'   => 10
        case 'b'   => 11
        case 'B'   => 11
        case 'c'   => 12
        case 'C'   => 12
        case 'd'   => 13
        case 'D'   => 13
        case 'e'   => 14
        case 'E'   => 14
        case 'f'   => 15
        case 'F'   => 15
        case other => error()
      }

    def parseSingleHex(offset: Int): Int = {
      val h = parseHex(offset)
      (h * 16) + h
    }

    def parseDoubleHex(offset: Int): Int =
      (parseHex(offset) * 16) + parseHex(offset + 1)

    def parseSingleAlpha(offset: Int): Double =
      parseSingleHex(offset).toDouble / 255.0

    def parseDoubleAlpha(offset: Int): Double =
      parseDoubleHex(offset).toDouble / 255.0

    val offset = if hex(0) == '#' then 1 else 0

    (hex.size - offset) match {
      case 3 =>
        Color.rgb(
          parseSingleHex(0 + offset),
          parseSingleHex(1 + offset),
          parseSingleHex(2 + offset)
        )
      case 4 =>
        Color.rgb(
          parseSingleHex(0 + offset),
          parseSingleHex(1 + offset),
          parseSingleHex(2 + offset),
          parseSingleAlpha(3 + offset)
        )
      case 6 =>
        Color.rgb(
          parseDoubleHex(0 + offset),
          parseDoubleHex(2 + offset),
          parseDoubleHex(4 + offset)
        )
      case 8 =>
        Color.rgb(
          parseDoubleHex(0 + offset),
          parseDoubleHex(2 + offset),
          parseDoubleHex(4 + offset),
          parseDoubleAlpha(6 + offset)
        )
      case other => error()
    }
  }
}
