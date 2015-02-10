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

  def leavesElement = {
    val color = green.spin((Math.random() * 30).degrees)
      .darken((Math.random() * 0.1).clip)
      .desaturate((Math.random() * 0.1).clip)
    Triangle(40, 40) lineColor(color) fillColor(color)
  }

  def row(elements: Int): Image =
    elements match {
      case 1 => bauble on leavesElement
      case n => bauble on leavesElement beside row(n - 1)
    }

  def leaves(levels: Int): Image =
    levels match {
      case 1 => leavesElement
      case n => row(n) below leaves(n - 1)
    }

  val leavesBackground = Triangle(40 * levels, 40 * levels) fillColor(darkGreen)
  val trunk = Rectangle(20,40) lineColor(brown) fillColor(brown)
  val tree = goldBauble above (leaves(levels) on leavesBackground) above trunk

  def ringOfBaubles(total: Int): Image = {
    val radius = 150
    def buildRing(n: Int): Image = {
      val angle =  math.Pi * 2 * n / total
      val bauble = Circle(7) lineColor(red) fillColor(red) at (math.cos(angle) * radius, math.sin(angle) * radius)
      n match {
        case 0 => bauble
        case n => bauble on buildRing(n - 1)
      }
    }
    buildRing(total)
  }

  val picture = tree on ringOfBaubles(7)
}
