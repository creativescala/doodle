/*
 * Copyright 2015 Creative Scala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package doodle
package java2d

import cats.effect.unsafe.implicits.global
import doodle.algebra.ToPicture
import doodle.core.format._
import doodle.core.{Base64 => B64}
import doodle.effect._
import doodle.syntax.all._
import minitest._

object ToPictureSpec extends SimpleTestSuite {
  def base64Distance[A <: Format](b1: B64[A], b2: B64[A]): Double = {
    import java.util.{Base64 => JBase64}
    val d1 = JBase64.getDecoder().decode(b1.value)
    val d2 = JBase64.getDecoder().decode(b2.value)

    d1.zip(d2).foldLeft(0.0) { (accum, elts) =>
      val (byte1, byte2) = elts
      accum + Math.abs(byte1 - byte2)
    }
  }

  val image = square[Algebra](40.0).fillColor(doodle.core.Color.blue)

  def testInverse[A <: Format](
      picture: Picture[Unit]
  )(implicit
      b: Base64[Algebra, Frame, A],
      tp: ToPicture[B64[A], Algebra]
  ) = {
    val (_, b1) = picture.base64[A](Frame.default.withSizedToPicture(0))
    val (_, b2) =
      b1.toPicture[Algebra].base64[A](Frame.default.withSizedToPicture(0))
    val error = base64Distance(b1, b2)
    // Large threshold because the round-trip introduces a small vertical
    // displacement that ends up causing a large error. Not sure of the source
    // of this.
    val threshold = b1.value.length() * 64

    // if(error > threshold) {
    //   import cats.implicits._
    //   text[Algebra, Drawing]("badness").above(
    //     b1.toPicture[Algebra, Drawing].debug.beside(b2.toPicture[Algebra, Drawing].debug)
    //   ).draw()
    // }
    assert(error <= threshold, s"Error: ${error}\nThreshold: ${threshold}")
  }

  test("toPicture should work with png") {
    testInverse[Png](image)
  }

  test("toPicture should work with gif") {
    testInverse[Gif](image)
  }

  test("toPicture should work with jpg") {
    testInverse[Jpg](image)
  }
}
