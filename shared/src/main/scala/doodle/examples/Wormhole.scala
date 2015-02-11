package doodle.examples

import doodle.core._
import doodle.syntax._

object Wormhole extends Drawable {
  def darken(color: Color): Color =
    color darken 0.05.normalized

  def bands(color: Color): Color =
    color spin 100.degrees darken 0.025.normalized

  def wormhole(radius: Double, color: Color, change: Color => Color): Image = {
    val angle  = radius.degrees
    val circle = Circle(radius).
      lineWidth(radius * 0.3).
      lineColor(color)

    if(radius > 10) {
      circle on (
        wormhole(radius * 0.9, change(color), change) at (
          angle.sin * 5,
          angle.cos * 5
        )
      )
    } else {
      circle
    }
  }

  def draw = wormhole(512, Color.orange, bands)
}