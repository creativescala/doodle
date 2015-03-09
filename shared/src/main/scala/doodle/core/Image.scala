package doodle.core

sealed trait Image {
  def beside(right: Image): Image =
    Beside(this, right)

  def on(bottom: Image): Image =
    Overlay(this, bottom)

  def under(top: Image): Image =
    Overlay(top, this)

  def above(bottom: Image): Image =
    Above(this, bottom)

  def below(top: Image): Image =
    Above(top, this)

  def at(v: Vec): Image =
    At(v, this)

  def at(x: Double, y: Double): Image =
    At(Vec(x, y), this)

  def lineColor(color: Color): Image =
    ContextTransform(_.lineColor(color), this)

  def lineWidth(width: Double): Image =
    ContextTransform(_.lineWidth(width), this)

  def fillColor(color: Color): Image =
    ContextTransform(_.fillColor(color), this)
}

final case class Path(elements: Seq[PathElement]) extends Image
final case class Circle(r: Double) extends Image
final case class Rectangle(w: Double, h: Double) extends Image
final case class Triangle(w: Double, h: Double) extends Image
final case class Beside(l: Image, r: Image) extends Image
final case class Above(l: Image, r: Image) extends Image
final case class Overlay(t: Image, b: Image) extends Image
final case class At(at: Vec, i: Image) extends Image
final case class ContextTransform(f: DrawingContext => DrawingContext, image: Image) extends Image
trait Drawable extends Image { def draw: Image }
