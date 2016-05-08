package doodle
package core

import doodle.core.font.Font

sealed abstract class Image extends Product with Serializable {
  def beside(right: Image): Image =
    Beside(this, right)

  def on(bottom: Image): Image =
    On(this, bottom)

  def under(top: Image): Image =
    On(top, this)

  def above(bottom: Image): Image =
    Above(this, bottom)

  def below(top: Image): Image =
    Above(top, this)

  def at(vec: Vec): Image =
    At(vec, this)

  def at(x: Double, y: Double): Image =
    At(Vec(x, y), this)

  def lineColor(color: Color): Image =
    ContextTransform(_.lineColor(color), this)

  def lineColorTransform(f: Color => Color): Image =
    ContextTransform(_.lineColorTransform(f), this)

  def lineWidth(width: Double): Image =
    ContextTransform(_.lineWidth(width), this)

  def fillColor(color: Color): Image =
    ContextTransform(_.fillColor(color), this)

  def fillColorTransform(f: Color => Color): Image =
    ContextTransform(_.fillColorTransform(f), this)

  def noLine: Image =
    ContextTransform(_.noLine, this)

  def noFill: Image =
    ContextTransform(_.noFill, this)

  def font(font: Font): Image =
    ContextTransform(_.font(font), this)
}
object Image {

  // Smart constructors

  def closedPath(elements: Seq[PathElement]): Image =
    ClosedPath(elements)

  def openPath(elements: Seq[PathElement]): Image =
    OpenPath(elements)

  def text(characters: String): Image =
    Text(characters)

  def circle(r: Double): Image =
    Circle(r)

  def rectangle(w: Double, h: Double): Image =
    Rectangle(w,h)

  def triangle(w: Double, h: Double): Image =
    Triangle(w,h)

  def empty: Image =
    Empty
}
sealed abstract class Path extends Image {
  def isOpen: Boolean =
    this match {
      case OpenPath(_)   => true
      case ClosedPath(_) => false
    }

  def isClosed: Boolean =
    !this.isOpen

  def open: Path =
    this match {
      case OpenPath(_)      => this
      case ClosedPath(elts) => OpenPath(elts)
    }

  def close: Path =
    this match {
      case OpenPath(elts) => ClosedPath(elts)
      case ClosedPath(_)  => this
    }
}
final case class OpenPath(elements: Seq[PathElement]) extends Path
final case class ClosedPath(elements: Seq[PathElement]) extends Path
final case class Text(get: String) extends Image
final case class Circle(r: Double) extends Image
final case class Rectangle(w: Double, h: Double) extends Image
final case class Triangle(w: Double, h: Double) extends Image
final case class Beside(l: Image, r: Image) extends Image
final case class Above(l: Image, r: Image) extends Image
final case class On(t: Image, b: Image) extends Image
final case class At(at: Vec, i: Image) extends Image
final case class ContextTransform(f: DrawingContext => DrawingContext, image: Image) extends Image
final case object Empty extends Image
