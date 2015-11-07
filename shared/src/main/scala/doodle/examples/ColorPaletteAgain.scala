package doodle.examples

import doodle.core._
import doodle.syntax._

object ColorPaletteAgain {

  val circleMinimum = 50
  val circleIncrement = 10

  def complement(c: Color): Color =
    c.spin(180.degrees)

  def nearComplement(c: Color): Color =
    c.spin(170.degrees)

  def analogous(c: Color): Color =
    c.spin(15.degrees)

  def singleCircle(n: Int, color: Color): Image =
    Circle(circleMinimum + circleIncrement * n) lineColor color lineWidth circleIncrement

  def complementCircles(n: Int, c: Color): Image = {
    val color = complement(c)
    if(n == 1) {
      singleCircle(n, color)
    } else {
      complementCircles(n - 1, color) on singleCircle(n, color)
    }
  }

  def nearComplementCircles(n: Int, c: Color): Image = {
    val color = nearComplement(c)
    if(n == 1) {
      singleCircle(n, color)
    } else {
      nearComplementCircles(n - 1, color) on singleCircle(n, color)
    }
  }

  def coloredCircles(n: Int, c: Color, palette: Color => Color): Image = {
    val color = palette(c)
    if(n == 1) {
      singleCircle(n, color)
    } else {
      coloredCircles(n - 1, color, palette) on singleCircle(n, color)
    }
  }

  def lcg1(input: Int): Int = {
    // These values of a, c, and m come from the Wikipedia page
    // on linear congruential generators and are reported to
    // be from Knuth. This a rather short period, but for our
    // purposes any period greater than 256 (2^8) will
    // generate sufficient results.
    val a = 8121
    val c = 28411
    val m = 134456
      (a * input + c) % m 
  }

  def lcg2(input: Int): Int = {
    // These values of a and m come from Park and Miller's Minimal Standard
    // Generator, and the method used to calculate the result is called
    // Schrage's method. Schrage's method avoids numeric overflow in the
    // computation. Both of these concepts I first read about in a post on the
    // Eternally Confuzzled blog.
    //
    // Learning about this was a fun excursion into Computer Science for me. I recommend it.
    val a = 48271
    val m = Int.MaxValue
    val q = m / a
    val r = m % a

    val result = a * (input % q) - r * (input / q)
    if(result <= 0)
      result + m
    else
      result
  }

  def normalize(value: Int, max: Int): Normalized =
    (value.toDouble / max.toDouble).normalized

  def rescale(value: Normalized, min: Double, range: Double): Normalized =
    ((value.get * range) + min).normalized

  def lcgColor(c: Color): Color = {
    val spun = c.spin(169.degrees)
    val saturation = rescale(normalize(lcg1(spun.hue.toDegrees.toInt), 134456), 0.25, 0.75)
    val lightness = rescale(normalize(lcg2(spun.hue.toDegrees.toInt), Int.MaxValue), 0.25, 0.5)
    println(s"saturation ${saturation} lightness ${lightness}")
    spun.saturation(saturation).lightness(lightness)
  }

  // def murmurColor(c: Color): Color = {
  //   val murmur = scala.util.hashing.MurmurHash3
  //   val spun = c.spin(169.degrees)
  //   val saturation = murmur.mix(murmur.symmetricSeed, spun.h.toDegrees.toInt)
  //   val lightness = murmur.mix(murmur.symmetricSeed, saturation)
  //   println(s"saturation ${normalize(saturation)} lightness ${normalize(lightness)}")
  //   spun.copy(s = calm(normalize(saturation)), l = calm(normalize(lightness)))
  // }

  def image =
    complementCircles(10, Color.seaGreen) beside
    nearComplementCircles(10, Color.seaGreen) beside
    coloredCircles(10, Color.seaGreen, lcgColor)
}
