package doodle.examples

import doodle.core._
import doodle.syntax._

object ColorPalette {
  // Type alias for cell constructor functions.
  // Takes a hue and a lightness as parameters:

  type CellFunc = (Int, Double) => Image

  // Different types of cell:

  def squareCell(size: Int): CellFunc =
    (hue: Int, lightness: Double) =>
      Image.rectangle(size, size) lineWidth 0 fillColor Color.hsl(hue.degrees, 1.0.normalized, lightness.normalized)

  def circleCell(size: Int): CellFunc =
    (hue: Int, lightness: Double) =>
      Image.circle(size/2) lineWidth 0 fillColor Color.hsl(hue.degrees, 1.0.normalized, lightness.normalized)

  // Code to construct a palette
  // from a given cell definition:

  def column(hue: Int, lStep: Double, cell: CellFunc): Image = {
    val cells =
      (0.0 until 1.0 by lStep).toList map { lightness =>
        cell(hue, lightness)
      }

    allAbove(cells)
  }

  def palette(hStep: Int, lStep: Double, cell: CellFunc): Image = {
    val columns =
      (0 until 360 by hStep).toList map { hue =>
        column(hue, lStep, cell)
      }

    allBeside(columns)
  }

  // The final palette:

  def image =
    palette(10, 0.04, circleCell(20))
}
