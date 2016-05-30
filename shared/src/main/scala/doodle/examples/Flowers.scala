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

  def square(minAlpha: Normalized, maxAlpha: Normalized): Normalized => Image =
    (r: Normalized) => {
      val alpha = (minAlpha.get + (r.get * (maxAlpha - minAlpha))).normalized
      rectangle(5, 5).fillColorTransform(_.alpha(alpha)).noLine
    }

  def point(
    position: Angle => Point,
    scale: Point => Point,
    image: Normalized => Image,
    rotation: Angle
  ): Angle => Image = {
    (angle: Angle) => {
      val pt = position(angle)
      val scaledPt = scale(pt)
      val r = pt.r.normalized
      val img = image(r)

      (img at scaledPt.toVec.rotate(rotation))
    }
  }


  def iterate(step: Angle): (Angle => Image) => Image = {
    (point: Angle => Image) => {
      def iter(angle: Angle): Image = {
        if(angle > Angle.one)
          Empty
        else
          point(angle) on iter(angle + step)
      }

      iter(Angle.zero)
    }
  }

  val image: Image = {
    val petals =
      iterate(1.degrees){
        point(
          position(5),
          scale(200.0),
          drop(2, 15, 0.5.normalized, 0.3.normalized, 1.0.normalized),
          0.degrees
        )
      }.fillColor(Color.fuchsia).lineColor(Color.fuchsia)

    val leaves =
      iterate(1.degrees){
        point(
          position(5),
          scale(150.0),
          square(0.3.normalized, 0.5.normalized),
          36.degrees)
      }.fillColor(Color.yellowGreen).lineColor(Color.yellowGreen)

    val background = (rectangle(500, 500) fillColor Color.black)

    petals on leaves on background
  }
}
