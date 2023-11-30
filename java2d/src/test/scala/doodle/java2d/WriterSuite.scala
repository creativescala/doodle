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
import doodle.core.format.*
import doodle.syntax.all.*
import munit.FunSuite

import java.io.File

class WriterSuite extends FunSuite {
  val image = circle[Algebra](20.0)

  test("write should work with png") {
    image.write[Png]("circle.png")
    val output = new File("circle.png")
    assertEquals(output.exists(), true)
    assertEquals(output.delete(), true)
  }

  test("write should work with gif") {
    image.write[Gif]("circle.gif")
    val output = new File("circle.gif")
    assertEquals(output.exists(), true)
    assertEquals(output.delete(), true)
  }

  test("write should work with pdf") {
    image.write[Pdf]("circle.pdf")
    val output = new File("circle.pdf")
    assertEquals(output.exists(), true)
    assertEquals(output.delete(), true)
  }

  test("write should work with jpg") {
    image.write[Jpg]("circle.jpg")
    val output = new File("circle.jpg")
    assertEquals(output.exists(), true)
    assertEquals(output.delete(), true)
  }
}
