package doodle
package image
package examples

import doodle.core._
import doodle.syntax.all._

object Flowers {
  def position(k: Int): Angle => Point =
    (angle: Angle) => {
      Point.cartesian(
        (angle * k.toDouble).cos * angle.cos,
        (angle * k.toDouble).cos * angle.sin
      )
    }

  def scale(factor: Double): Point => Point =
    (pt: Point) => {
      Point.polar(pt.r * factor, pt.angle)
    }

  def drop(
      minRadius: Int,
      maxRadius: Int,
      ratio: Normalized
  ): Normalized => Image =
    (r: Normalized) => {
      val size = minRadius + (r.get * (maxRadius - minRadius))
      // val alpha = (minAlpha.get + (r.get * (maxAlpha - minAlpha))).normalized

      (Image.circle(size * ratio.get).noStroke on Image.circle(size).noFill)
      // fillColorTransform(_.alpha(alpha)).
      // strokeColorTransform(_.alpha(alpha))
    }

  def square: Normalized => Image =
    (_: Normalized) => {
      // val alpha = (minAlpha.get + (r.get * (maxAlpha - minAlpha))).normalized
      Image.rectangle(5, 5).noStroke //.fillColorTransform(_.alpha(alpha))
    }

  def point(
      position: Angle => Point,
      scale: Point => Point,
      image: Normalized => Image,
      rotation: Angle
  ): Angle => Image = { (angle: Angle) =>
    {
      val pt = position(angle)
      val scaledPt = scale(pt)
      val r = pt.r.normalized
      val img = image(r)

      (img at scaledPt.toVec.rotate(rotation))
    }
  }

  def iterate(step: Angle): (Angle => Image) => Image = {
    (point: Angle => Image) =>
      {
        def iter(angle: Angle): Image = {
          if (angle > Angle.one)
            Image.empty
          else
            point(angle) on iter(angle + step)
        }

        iter(Angle.zero)
      }
  }

  val image: Image = {
    val petals =
      iterate(1.degrees) {
        point(
          position(5),
          scale(200.0),
          drop(2, 15, 0.5.normalized),
          0.degrees
        )
      }.fillColor(Color.fuchsia).strokeColor(Color.fuchsia)

    val leaves =
      iterate(1.degrees) {
        point(position(5), scale(150.0), square, 36.degrees)
      }.fillColor(Color.yellowGreen).strokeColor(Color.yellowGreen)

    val background = (Image.rectangle(500, 500).fillColor(Color.black))

    petals on leaves on background
  }
}
