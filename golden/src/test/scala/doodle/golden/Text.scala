package doodle
package golden

import cats.implicits._
import doodle.core.font._
import doodle.java2d._
import doodle.syntax._
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
}
