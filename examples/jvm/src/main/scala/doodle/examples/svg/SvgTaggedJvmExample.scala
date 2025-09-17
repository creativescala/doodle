package doodle.examples.svg

import doodle.core.Color
import doodle.svg.*
import doodle.svg.effect.Frame
import doodle.svg.syntax.all.*
import doodle.syntax.all.*
import cats.effect.unsafe.implicits.global
import doodle.effect.DefaultFrame

object SvgTaggedJvmExample {

  implicit val frame: doodle.effect.DefaultFrame[Frame] =
    new DefaultFrame[Frame] {
      override def default: Frame = doodle.svg.effect.Frame("frame1")
    }

  def drawLink(id: String): Unit =
    Picture
      .regularPolygon(7, 15)
      .strokeWidth(5.0)
      .beside(Picture.text("Creative Scala").noStroke.fillColor(Color.black))
      .link("https://creativescala.org/")
      .write("link_tagged.svg")
}
