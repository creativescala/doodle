package doodle
package image
package examples

import doodle.core._
import doodle.image._
import doodle.syntax._

object TimeSeries {
  def ballOnStick(length: Int, ballColor: Color, stickColor: Color): Image = {
    val ball = Image.circle(10).strokeWidth(5).strokeColor(ballColor).noFill
    val stick =
      Image.line(0, length.toDouble).strokeWidth(5).strokeColor(stickColor)

    // shift origin back to the base of the ball-on-stick, so alignment is easier
    (ball above stick).at(0, (length + 10) / 2.0)
  }

  object resample {
    val first = ballOnStick(80, Color.orange, Color.orange)
    val firstResampled = ballOnStick(80, Color.cyan, Color.cyan)
    val second = ballOnStick(120, Color.orange, Color.orange)
    val secondResampled = ballOnStick(120, Color.cyan, Color.cyan)
    val ghost = Image.square(25).noStroke.noFill
    val spacer = Image.rectangle(40, 10).noFill.noStroke

    val before =
      first
        .beside(spacer)
        .beside(ghost)
        .beside(spacer)
        .beside(ghost)
        .beside(spacer)
        .beside(second)
        .beside(spacer)
        .beside(ghost)
        .beside(spacer)
        .beside(first)

    val after =
      first
        .beside(spacer)
        .beside(firstResampled)
        .beside(spacer)
        .beside(firstResampled)
        .beside(spacer)
        .beside(second)
        .beside(spacer)
        .beside(secondResampled)
        .beside(spacer)
        .beside(first)
  }

  object similaritySearch {
    val one = ballOnStick(80, Color.orange, Color.orange)
    val two = ballOnStick(120, Color.orange, Color.orange)
    val three = ballOnStick(60, Color.orange, Color.orange)
    val spacer = Image.rectangle(40, 10).noFill.noStroke

    val signal =
      one
        .beside(spacer)
        .beside(two)
        .beside(spacer)
        .beside(three)
        .beside(spacer)
        .beside(one)
        .beside(spacer)
        .beside(two)
        .beside(spacer)
        .beside(two)

    val patternColor = Color.cyan.alpha(0.8.normalized)
    val patternOne = ballOnStick(80, patternColor, patternColor)
    val patternTwo = ballOnStick(120, patternColor, patternColor)

    val pattern =
      patternOne
        .beside(spacer)
        .beside(patternTwo)
        .beside(spacer)
        .beside(patternTwo)

    val first = signal under (pattern.at(-97.5, 0))
    val second = signal under (pattern.at(-32.5, 0))
    val third = signal under (pattern.at(32.5, 0))
    val fourth = signal under (pattern.at(97.5, 0))
  }

  object distribution {
    val input =
      Image.rectangle(600, 120).noStroke.fillColor(Color.orange)

    val splitSeries =
      Image.rectangle(150, 120).noStroke

    def color(hue: Angle): Color =
      Color.hsl(hue, 1.0, 0.5)

    val fullSplit =
      splitSeries
        .fillColor(color(0.degrees))
        .beside(splitSeries.fillColor(color(90.degrees)))
        .beside(splitSeries.fillColor(color(180.degrees)))
        .beside(splitSeries.fillColor(color(270.degrees)))

    val spacer = Image.rectangle(150, 120).noStroke.noFill
    def processed(hue: Angle): Image =
      Image
        .star(5, 60, 30, 30.degrees)
        .noStroke
        .fillColor(color(hue))
        .on(spacer)

    val processedSplit =
      processed(0.degrees)
        .beside(processed(90.degrees))
        .beside(processed(180.degrees))
        .beside(processed(270.degrees))

    val output =
      processed(Color.orange.hue)
  }
}
