package doodle.examples

import doodle.core._
import doodle.syntax.angle._
import doodle.syntax.normalized._

object ChristmasTree {
  import Color._

  val redBauble  = Circle(7)  lineWidth 0 fillColor red
  val goldBauble = Circle(10) lineWidth 0 fillColor gold

  def treeElement = {
    val color = green
      .spin((math.random * 30).degrees)
      .darken((math.random * 0.1).normalized)
      .desaturate((math.random * 0.1).normalized)

    Triangle(40, 40) lineWidth 0 fillColor color
  }

  def row(elements: Int): Image =
    elements match {
      case 1 => redBauble on treeElement
      case n => redBauble on treeElement beside row(n - 1)
    }

  def tree(levels: Int): Image =
    levels match {
      case 1 => treeElement
      case n => row(n) below tree(n - 1)
    }

  val levels  = 4
  val foliage = Triangle(40 * levels, 40 * levels) fillColor darkGreen
  val trunk   = Rectangle(20,40) lineColor brown fillColor brown

  val image = goldBauble above ( tree(levels) on foliage ) above trunk
}
