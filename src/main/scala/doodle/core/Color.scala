/*
 * Copyright 2015 noelwelsh
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

import doodle.syntax.angle._
import doodle.syntax.normalized._
import doodle.syntax.unsignedByte._

sealed abstract class Color extends Product with Serializable {
  import Color._

  // Accessors ----------------------------------------------

  def red: UnsignedByte =
    this.toRGBA.r

  def green: UnsignedByte =
    this.toRGBA.g

  def blue: UnsignedByte =
    this.toRGBA.b

  def hue: Angle =
    this.toHSLA.h

  def saturation: Normalized =
    this.toHSLA.s

  def lightness: Normalized =
    this.toHSLA.l

  def alpha: Normalized =
    this match {
      case RGBA(_, _, _, a) => a
      case HSLA(_, _, _, a) => a
    }

  // Color manipulation ------------------------------------

  /** Copies this color, changing the hue to the given value*/
  def hue(angle: Angle): Color =
    this.toHSLA.copy(h = angle)

  /** Copies this color, changing the saturation to the given value*/
  def saturation(s: Normalized): Color =
    this.toHSLA.copy(s = s)

  /** Copies this color, changing the lightness to the given value*/
  def lightness(l: Normalized): Color =
    this.toHSLA.copy(l = l)

  /** Copies this color, changing the alpha to the given value*/
  def alpha(a: Normalized): Color =
    this.toHSLA.copy(a = a)


  /** Rotate hue by the given angle */
  def spin(angle: Angle) = {
    val original = this.toHSLA
    original.copy(h = original.h + angle)
  }

  /** Lighten the color by the given amount. This is an absolute
    * amount, not an amount relative to the Color's current
    * lightness. Lightness is clipped at Normalized.MaxValue */
  def lighten(lightness: Normalized) = {
    val original = this.toHSLA
    original.copy(l = Normalized.clip(original.l + lightness))
  }

  /** Darken the color by the given amount. This is an absolute
    * amount, not an amount relative to the Color's current
    * lightness. Lightness is clipped at Normalized.MaxValue */
  def darken(darkness: Normalized) = {
    val original = this.toHSLA
    original.copy(l = Normalized.clip(original.l - darkness))
  }

  /** Saturate the color by the given amount. This is an absolute
    * amount, not an amount relative to the Color's current
    * saturation. Saturation is clipped at Normalized.MaxValue */
  def saturate(saturation: Normalized) = {
    val original = this.toHSLA
    original.copy(s = Normalized.clip(original.s + saturation))
  }

  /** Desaturate the color by the given amount. This is an absolute
    * amount, not an amount relative to the Color's current
    * saturation. Saturation is clipped at Normalized.MaxValue */
  def desaturate(desaturation: Normalized) = {
    val original = this.toHSLA
    original.copy(s = Normalized.clip(original.s - desaturation))
  }

  /** Increase the alpha channel by the given amount. */
  def fadeIn(opacity: Normalized) = {
    val original = this.toHSLA
    original.copy(a = Normalized.clip(original.a + opacity))
  }

  /** Decrease the alpha channel by the given amount. */
  def fadeOut(opacity: Normalized) = {
    val original = this.toHSLA
    original.copy(a = Normalized.clip(original.a - opacity))
  }

  /** Lighten the color by the given *relative* amount. For example, calling
    * `aColor.lightenBy(0.1.normalized` increases the lightness by 10% of the
    * current lightness.
    */
  def lightenBy(lightness: Normalized) = {
    val original = this.toHSLA
    original.copy(l = Normalized.clip(original.l.get * (1 + lightness.get)))
  }

  /** Darken the color by the given *relative* amount. For example, calling
    * `aColor.darkenBy(0.1.normalized` decreases the lightness by 10% of the
    * current lightness.
    */
  def darkenBy(darkness: Normalized) = {
    val original = this.toHSLA
    original.copy(l = Normalized.clip(original.l.get * (1 - darkness.get)))
  }

  /** Saturate the color by the given *relative* amount. For example, calling
    * `aColor.saturateBy(0.1.normalized` increases the saturation by 10% of the
    * current saturation.
    */
  def saturateBy(saturation: Normalized) = {
    val original = this.toHSLA
    original.copy(s = Normalized.clip(original.s.get * (1 + saturation.get)))
  }

  /** Desaturate the color by the given *relative* amount. For example, calling
    * `aColor.desaturateBy(0.1.normalized` decreases the saturation by 10% of the
    * current saturation.
    */
  def desaturateBy(desaturation: Normalized) = {
    val original = this.toHSLA
    original.copy(s = Normalized.clip(original.s.get * (1 - desaturation.get)))
  }

  /** Increase the alpha channel by the given relative amount. */
  def fadeInBy(opacity: Normalized) = {
    val original = this.toHSLA
    original.copy(a = Normalized.clip(original.a.get * (1 + opacity.get)))
  }

  /** Decrease the alpha channel by the given relative amount. */
  def fadeOutBy(opacity: Normalized) = {
    val original = this.toHSLA
    original.copy(a = Normalized.clip(original.a.get * (1 - opacity.get)))
  }

  // Other -------------------------------------------------

  /** True if this is approximately equal to that */
  def ~=(that: Color): Boolean =
    (this.toRGBA, that.toRGBA) match {
      case (RGBA(r1, g1, b1, a1), RGBA(r2, g2, b2, a2)) =>
        Math.abs(r1 - r2) < 2 &&
        Math.abs(g1 - g2) < 2 &&
        Math.abs(b1 - b2) < 2 &&
        Math.abs(a1 - a2) < 0.1
    }

  def toHSLA: HSLA =
    this match {
      case RGBA(r, g, b, a) =>
        val rNormalized = r.toNormalized
        val gNormalized = g.toNormalized
        val bNormalized = b.toNormalized
        val cMax = rNormalized max gNormalized max bNormalized
        val cMin = rNormalized min gNormalized min bNormalized
        val delta = cMax - cMin

        val unnormalizedHue =
          if(cMax == rNormalized)
            60 * (((gNormalized - bNormalized) / delta))
          else if(cMax == gNormalized)
            60 * (((bNormalized - rNormalized) / delta) + 2)
          else
            60 * (((rNormalized - gNormalized) / delta) + 4)
        val hue = unnormalizedHue.degrees

        val lightness = Normalized.clip((cMax + cMin) / 2)

        val saturation =
          if(delta == 0.0)
            Normalized.MinValue
          else
            Normalized.clip(delta / (1 - Math.abs(2 * lightness.get - 1)))

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
            def hueToRgb(p: Double, q: Double, t: Normalized): Normalized = {
              Normalized.wrap(t.get match {
                case t if t < 1.0/6.0 => p + (q - p) * 6 * t
                case t if t < 0.5 => q
                case t if t < 2.0/3.0 => p + (q - p) * (2.0/3.0 - t) * 6
                case _ => p
              })
            }

            val lightness = l.get
            val q =
              if(lightness < 0.5)
                lightness * (1 + s)
              else
                lightness + s - (lightness * s)
            val p = 2 * lightness - q
            val r = hueToRgb(p, q, Normalized.wrap((h + 120.degrees).toTurns))
            val g = hueToRgb(p, q, Normalized.wrap(h.toTurns))
            val b = hueToRgb(p, q, Normalized.wrap((h - 120.degrees).toTurns))

            RGBA(r.toUnsignedByte, g.toUnsignedByte, b.toUnsignedByte, a)
        }
    }
}
object Color extends CommonColors {
  final case class RGBA(r: UnsignedByte, g: UnsignedByte, b: UnsignedByte, a: Normalized) extends Color
  final case class HSLA(h: Angle, s: Normalized, l: Normalized, a: Normalized) extends Color

  def rgba(r: UnsignedByte, g: UnsignedByte, b: UnsignedByte, a: Normalized): Color =
    RGBA(r, g, b, a)

  def rgba(r: Int, g: Int, b: Int, a: Double): Color =
    RGBA(r.uByte, g.uByte, b.uByte, a.normalized)

  def hsla(h: Angle, s: Double, l: Double, a: Double): Color =
    HSLA(h, s.normalized, l.normalized, a.normalized)


  def rgb(r: UnsignedByte, g: UnsignedByte, b: UnsignedByte): Color =
    rgba(r, g, b, 1.0.normalized)

  def rgb(r: Int, g: Int, b: Int): Color =
    rgba(r, g, b, 1.0)

  def hsl(h: Angle, s: Double, l: Double): Color =
    hsla(h, s, l, 1.0)

}
