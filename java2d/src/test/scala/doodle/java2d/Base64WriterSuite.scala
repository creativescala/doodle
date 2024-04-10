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
import doodle.core.Base64
import doodle.core.format._
import doodle.syntax.all._
import munit.FunSuite

class Base64WriterSuite extends FunSuite {
  def base64Distance[A <: Format](b1: Base64[A], b2: Base64[A]): Double = {
    import java.util.{Base64 => JBase64}
    val d1 = JBase64.getDecoder().decode(b1.value)
    val d2 = JBase64.getDecoder().decode(b2.value)

    d1.zip(d2).foldLeft(0.0) { (accum, elts) =>
      val (byte1, byte2) = elts
      accum + Math.abs(byte1 - byte2)
    }
  }

  val image = circle(20.0)

  test("base64 should work with png") {
    val (_, b1) = image.base64[Png]()
    val (_, b2) = image.base64[Png]()

    assert(base64Distance(b1, b2) <= (b1.value.length * 2))
  }

  test("base64 should work with gif") {
    val (_, b1) = image.base64[Gif]()
    val (_, b2) = image.base64[Gif]()

    assert(base64Distance(b1, b2) <= (b1.value.length * 2))
  }

  test("base64 should work with jpg") {
    val (_, b1) = image.base64[Jpg]()
    val (_, b2) = image.base64[Jpg]()

    assert(base64Distance(b1, b2) <= (b1.value.length * 2))
  }
}
