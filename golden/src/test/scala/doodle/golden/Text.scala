package doodle
package golden

import doodle.core.font._
import doodle.java2d._
import doodle.syntax._
import munit._

class Text extends FunSuite with Golden {
  testPicture("text-serif-default") {
    text[Algebra, Drawing]("Hi!").font(Font.defaultSerif)
  }

  testPicture("text-sans-serif-default") {
    text[Algebra, Drawing]("Hi!").font(Font.defaultSansSerif)
  }

  testPicture("text-serif-48pt") {
    text[Algebra, Drawing]("Hi!").font(Font.defaultSerif.size(48))
  }

  testPicture("text-sans-serif-48pt") {
    text[Algebra, Drawing]("Hi!").font(Font.defaultSansSerif.size(48))
  }
}
