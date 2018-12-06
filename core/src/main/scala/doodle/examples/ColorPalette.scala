package doodle.examples

import scala.math.BigDecimal

import cats.instances.list._
import doodle.core._
import doodle.image.Image
import doodle.syntax._

object ColorPalette {
  // Type alias for cell constructor functions.
  // Takes a hue and a lightness as parameters:

  type CellFunc = (Int, Double) => Image

  // Different types of cell:

  def squareCell(size: Int): CellFunc =
    (hue: Int, lightness: Double) =>
      Image
        .rectangle(size.toDouble, size.toDouble)
        .strokeWidth(0)
        .fillColor(Color.hsl(hue.degrees, 1.0, lightness))

  def circleCell(size: Int): CellFunc =
    (hue: Int, lightness: Double) =>
      Image
        .circle(size / 2.0)
        .strokeWidth(0)
        .fillColor(Color.hsl(hue.degrees, 1.0, lightness))

  // Code to construct a palette
  // from a given cell definition:

  def column(hue: Int, lStep: Double, cell: CellFunc): Image = {
    val cells =
      (BigDecimal(0.0) until 1.0 by lStep).toList map { lightness =>
        cell(hue, lightness.doubleValue)
      }

    cells.allAbove
  }

  def palette(hStep: Int, lStep: Double, cell: CellFunc): Image = {
    val columns =
      (0 until 360 by hStep).toList map { hue =>
        column(hue, lStep, cell)
      }

    columns.allBeside
  }

  // The final palette:

  def image =
    palette(10, 0.04, circleCell(20))
}
