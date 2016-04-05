package doodle
package examples

import doodle.core._
import doodle.core.Image._
import doodle.syntax._

object Flowers {
  def position(k: Int): Angle => Point =
    (angle: Angle) => {
      Point.cartesian((angle * k).cos * angle.cos, (angle * k).cos * angle.sin)
    }

  def scale(factor: Double): Point => Point =
    (pt: Point) => {
      Point.polar(pt.r * factor, pt.angle)
    }

  def drop(minRadius: Int, maxRadius: Int, ratio: Normalized, minAlpha: Normalized, maxAlpha: Normalized): Normalized => Image =
    (r: Normalized) => {
      val size = minRadius + (r.get * (maxRadius - minRadius))
      val alpha = (minAlpha.get + (r.get * (maxAlpha - minAlpha))).normalized

      (circle(size * ratio.get).noLine on circle(size).noFill).
        fillColorTransform(_.alpha(alpha)).
        lineColorTransform(_.alpha(alpha))
    }

  def point(rotation: Angle): Angle => Image = {
    val ptGen = position(5)
    val scaleGen = scale(200.0)
    val dropGen = drop(2, 15, 0.5.normalized, 0.3.normalized, 1.0.normalized)

    (angle: Angle) => {
      val pt = ptGen(angle)
      val r = pt.r.normalized
      val drop = dropGen(r)

      val scaledPt = scaleGen(pt)

      (drop at scaledPt.toVec.rotate(rotation))
    }
  }


  def iterate(step: Angle): (Angle => Image) => Image = {
    (parametric: Angle => Image) => {
      def iter(angle: Angle): Image = {
        if(angle > Angle.One)
          Empty
        else
          parametric(angle) on iter(angle + step)
      }

      iter(Angle.Zero)
    }
  }

  val image: Image = {
    iterate(1.degrees){ point(0.degrees) }.
      fillColor(Color.fuchsia).
      lineColor(Color.fuchsia) on
    (rectangle(500, 500) fillColor Color.black)
  }
}
