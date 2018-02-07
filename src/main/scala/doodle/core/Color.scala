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
import scala.annotation.tailrec

sealed abstract class Color extends Product with Serializable {

  // Accessors ----------------------------------------------

  def red: Int =
    this.toRGBA.r

  def green: Int =
    this.toRGBA.g

  def blue: Int =
    this.toRGBA.b

  def hue: Angle =
    this.toHSLA.h

  def saturation: Double =
    this.toHSLA.s

  def lightness: Double =
    this.toHSLA.l

  def alpha: Double =
    this match {
      case RGBA(_, _, _, a) => a
      case HSLA(_, _, _, a) => a
    }

  // Color manipulation ------------------------------------

  /** Copies this color, changing the hue to the given value*/
  def hue(angle: Angle): Color =
    this.toHSLA.copy(h = angle)

  /** Copies this color, changing the saturation to the given value*/
  def saturation(s: Double): Color =
    this.toHSLA.copy(s = s)

  /** Copies this color, changing the lightness to the given value*/
  def lightness(l: Double): Color =
    this.toHSLA.copy(l = l)

  /** Copies this color, changing the alpha to the given value*/
  def alpha(a: Double): Color =
    this.toHSLA.copy(a = a)


  /** Rotate hue by the given angle */
  def spin(angle: Angle) = {
    val original = this.toHSLA
    original.copy(h = original.h + angle)
  }

  /** Lighten the color by the given amount. This is an absolute
    * amount, not an amount relative to the Color's current
    * lightness. Lightness is clipped at Double.MaxValue */
  def lighten(lightness: Double) = {
    val original = this.toHSLA
    original.copy(l = Color.clip(original.l + lightness))
  }

  /** Darken the color by the given amount. This is an absolute
    * amount, not an amount relative to the Color's current
    * lightness. Lightness is clipped at Double.MaxValue */
  def darken(darkness: Double) = {
    val original = this.toHSLA
    original.copy(l = Color.clip(original.l - darkness))
  }

  /** Saturate the color by the given amount. This is an absolute
    * amount, not an amount relative to the Color's current
    * saturation. Saturation is clipped at Double.MaxValue */
  def saturate(saturation: Double) = {
    val original = this.toHSLA
    original.copy(s = Color.clip(original.s + saturation))
  }

  /** Desaturate the color by the given amount. This is an absolute
    * amount, not an amount relative to the Color's current
    * saturation. Saturation is clipped at Double.MaxValue */
  def desaturate(desaturation: Double) = {
    val original = this.toHSLA
    original.copy(s = Color.clip(original.s - desaturation))
  }

  /** Increase the alpha channel by the given amount. */
  def fadeIn(opacity: Double) = {
    val original = this.toHSLA
    original.copy(a = Color.clip(original.a + opacity))
  }

  /** Decrease the alpha channel by the given amount. */
  def fadeOut(opacity: Double) = {
    val original = this.toHSLA
    original.copy(a = Color.clip(original.a - opacity))
  }

  /** Lighten the color by the given *relative* amount. For example, calling
    * `aColor.lightenBy(0.1)` increases the lightness by 10% of the
    * current lightness.
    */
  def lightenBy(lightness: Double) = {
    val original = this.toHSLA
    original.copy(l = Color.clip(original.l * (1 + lightness)))
  }

  /** Darken the color by the given *relative* amount. For example, calling
    * `aColor.darkenBy(0.1)` decreases the lightness by 10% of the
    * current lightness.
    */
  def darkenBy(darkness: Double) = {
    val original = this.toHSLA
    original.copy(l = Color.clip(original.l * (1 - darkness)))
  }

  /** Saturate the color by the given *relative* amount. For example, calling
    * `aColor.saturateBy(0.1)` increases the saturation by 10% of the
    * current saturation.
    */
  def saturateBy(saturation: Double) = {
    val original = this.toHSLA
    original.copy(s = Color.clip(original.s * (1 + saturation)))
  }

  /** Desaturate the color by the given *relative* amount. For example, calling
    * `aColor.desaturateBy(0.1)` decreases the saturation by 10% of the
    * current saturation.
    */
  def desaturateBy(desaturation: Double) = {
    val original = this.toHSLA
    original.copy(s = Color.clip(original.s * (1 - desaturation)))
  }

  /** Increase the alpha channel by the given relative amount. */
  def fadeInBy(opacity: Double) = {
    val original = this.toHSLA
    original.copy(a = Color.clip(original.a * (1 + opacity)))
  }

  /** Decrease the alpha channel by the given relative amount. */
  def fadeOutBy(opacity: Double) = {
    val original = this.toHSLA
    original.copy(a = Color.clip(original.a * (1 - opacity)))
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
        val rDouble = r.toDouble
        val gDouble = g.toDouble
        val bDouble = b.toDouble
        val cMax = rDouble max gDouble max bDouble
        val cMin = rDouble min gDouble min bDouble
        val delta = cMax - cMin

        val unnormalizedHue =
          if(cMax == rDouble)
            60 * (((gDouble - bDouble) / delta))
          else if(cMax == gDouble)
            60 * (((bDouble - rDouble) / delta) + 2)
          else
            60 * (((rDouble - gDouble) / delta) + 4)
        val hue = unnormalizedHue.degrees

        val lightness = Color.clip((cMax + cMin) / 2)

        val saturation =
          if(delta == 0.0)
            Double.MinValue
          else
            Color.clip(delta / (1 - Math.abs(2 * lightness - 1)))

        HSLA(hue, saturation, lightness, a)

      case HSLA(h, s, l, a) => HSLA(h, s, l, a)
    }

  def toRGBA: RGBA =
    this match {
      case RGBA(r, g, b, a) => RGBA(r, g, b, a)
      case HSLA(h, s, l, a) =>
        s match {
          case 0 =>
            val lightness = l.toInt
            RGBA(lightness, lightness, lightness, a)
          case s =>
            def hueToRgb(p: Double, q: Double, t: Double): Double = {
              Color.wrap(t match {
                case t if t < 1.0/6.0 => p + (q - p) * 6 * t
                case t if t < 0.5 => q
                case t if t < 2.0/3.0 => p + (q - p) * (2.0/3.0 - t) * 6
                case _ => p
              })
            }

            val lightness = l
            val q =
              if(lightness < 0.5)
                lightness * (1 + s)
              else
                lightness + s - (lightness * s)
            val p = 2 * lightness - q
            val r = hueToRgb(p, q, Color.wrap((h + 120.degrees).toTurns))
            val g = hueToRgb(p, q, Color.wrap(h.toTurns))
            val b = hueToRgb(p, q, Color.wrap((h - 120.degrees).toTurns))

            RGBA(r.toInt, g.toInt, b.toInt, a)
        }
    }
}
final case class RGBA(r: Int, g: Int, b: Int, a: Double) extends Color
final case class HSLA(h: Angle, s: Double, l: Double, a: Double) extends Color

object Color extends CommonColors {
  def rgba(r: Int, g: Int, b: Int, a: Double): Color =
    RGBA(r, g, b, a)

  def hsla(h: Angle, s: Double, l: Double, a: Double): Color =
    HSLA(h, s, l, a)

  def rgb(r: Int, g: Int, b: Int): Color =
    rgba(r, g, b, 1.0)

  def hsl(h: Angle, s: Double, l: Double): Color =
    hsla(h, s, l, 1.0)

  /** Utility for dealing with values normalized to between 0 and 1 inclusive */
  def clip(value: Double): Double =
    value match {
      case _ if value < 0.0 => 0.0
      case _ if value > 1.0 => 1.0
      case v => v
    }

  /** Utility for dealing with values normalized to between 0 and 1 inclusive */
  def wrap(value: Double): Double = {
    @tailrec def loop(value: Double): Double = value match {
      case v if v > 1.0 => loop(v - 1.0)
      case v if v < 0.0 => loop(v + 1.0)
      case v => v
    }

    loop(value)
  }
}
