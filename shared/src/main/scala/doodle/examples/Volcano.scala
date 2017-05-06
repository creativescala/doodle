package doodle
package examples

import doodle.core._
import doodle.core.Image._
import doodle.syntax._
import doodle.random._
import cats.syntax.cartesian._

object Volcano {
  def rose(k: Int): Angle => Point =
    (angle: Angle) => {
      Point.cartesian((angle * k).cos * angle.cos, (angle * k).cos * angle.sin)
    }

  def scale(factor: Double): Point => Point =
    (pt: Point) => {
      Point.polar(pt.r * factor, pt.angle)
    }

  def jitter(point: Point): Random[Point] = {
    val noise = Random.normal(0, 10.0)

    (noise |@| noise) map { (dx, dy) =>
      Point.cartesian(point.x + dx, point.y + dy)
    }
  }

  def smoke(r: Normalized): Random[Image] = {
    val alpha = Random.normal(0.7, 0.3) map (a => a.normalized)
    val hue = Random.double.map(h => (h * 0.1).turns)
    val saturation = Random.double.map(s => (s * 0.8).normalized)
    val lightness = Random.normal(0.8, 0.4) map (a => a.normalized)
    val color =
      (hue |@| saturation |@| lightness |@| alpha) map {
        (h, s, l, a) => Color.hsla(h, s, l, a)
      }
    val c = Random.normal(5, 5) map (r => circle(r))

    (c |@| color) map { (circle, line) => circle.lineColor(line).noFill }
  }

  def point(
    position: Angle => Point,
    scale: Point => Point,
    jitter: Point => Random[Point],
    image: Normalized => Random[Image],
    rotation: Angle
  ): Angle => Random[Image] = {
    (angle: Angle) => {
      val pt = position(angle)
      val scaledPt = scale(pt)
      val jitteredPt = jitter(scaledPt)

      val r = pt.r.normalized
      val img = image(r)

      (img |@| jitteredPt) map { (i, pt) =>
        i at pt.toVec.rotate(rotation)
      }
    }
  }

  def iterate(step: Angle): (Angle => Random[Image]) => Random[Image] = {
    (point: Angle => Random[Image]) => {
      def iter(angle: Angle): Random[Image] = {
        if(angle > Angle.one)
          Random.always(Image.empty)
        else
          point(angle) |@| iter(angle + step) map { _ on _ }
      }

      iter(Angle.zero)
    }
  }

  val image: Random[Image] = {
    val pts =
      for(i <- 28 to 360 by 39) yield {
        iterate(1.degrees){
          point(
            rose(5),
            scale(i),
            jitter _,
            smoke _,
            i.degrees
          )
        }
      }
    val picture = pts.foldLeft(Random.always(Image.empty)){ (accum, img) =>
      (accum |@| img) map { _ on _ }
    }
    val background = (rectangle(650, 650) fillColor Color.black)

    picture map { _ on background }
  }
}
