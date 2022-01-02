package doodle
package golden

import cats.implicits._
import doodle.core._
import doodle.core.font._
import doodle.java2d._
import doodle.syntax.all._
import munit._

class Text extends FunSuite with GoldenPicture {
  // Enusre the text images have the same size, which guards to some extent
  // against different font rendering on different machines
  val spacer = square[Algebra, Drawing](100).noFill.noStroke

  testPicture("text-serif-default") {
    text[Algebra, Drawing]("Hi!").font(Font.defaultSerif).on(spacer)
  }

  testPicture("text-sans-serif-default") {
    text[Algebra, Drawing]("Hi!").font(Font.defaultSansSerif).on(spacer)
  }

  testPicture("text-serif-48pt") {
    text[Algebra, Drawing]("Hi!").font(Font.defaultSerif.size(48)).on(spacer)
  }

  testPicture("text-sans-serif-48pt") {
    text[Algebra, Drawing]("Hi!")
      .font(Font.defaultSansSerif.size(48))
      .on(spacer)
  }

  // Test that layout is correct. Text should be centered on the circle
  testPicture("text-on-circle") {
    text[Algebra, Drawing]("Hi!")
      .on(circle[Algebra, Drawing](40))
  }

  testPicture("text-color") {
    text[Algebra, Drawing]("Red")
      .strokeColor(Color.red)
      .beside(text[Algebra, Drawing]("Blue").strokeColor(Color.blue))
      .font(Font.defaultSerif.size(24).family("Arial"))
      .on(rectangle(115, 50).fillColor(Color.white).noStroke)
  }
}
