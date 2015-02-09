package doodle

import doodle.core._
import doodle.syntax.angle._
import doodle.syntax.normalised._

object Example {
  val red = Color.rgb(255, 0, 0)
  val gold = Color.rgb(255, 255, 0)
  val green = Color.rgb(0, 255, 0)
  val brown = Color.rgb(169, 69, 19)
  val darkGreen = green.darken(0.3.clip)

  val bauble = Circle(7) lineColor(red) fillColor(red)
  val goldBauble = Circle(10) lineColor(gold) fillColor(gold)

  val levels = 4

  def treeElement = {
    val color = green.spin((Math.random() * 30).degrees)
      .darken((Math.random() * 0.1).clip)
      .desaturate((Math.random() * 0.1).clip)
    Triangle(40, 40) lineColor(color) fillColor(color)
  }

  def row(elements: Int): Image =
    elements match {
      case 1 => bauble on treeElement
      case n => bauble on treeElement beside row(n - 1)
    }

  def tree(levels: Int): Image =
    levels match {
      case 1 => treeElement
      case n => row(n) below tree(n - 1)
    }

  val treeBackground = Triangle(40 * levels, 40 * levels) fillColor(darkGreen)
  val trunk = Rectangle(20,40) lineColor(brown) fillColor(brown)
  val picture = goldBauble above (tree(levels) on treeBackground) above trunk
}
