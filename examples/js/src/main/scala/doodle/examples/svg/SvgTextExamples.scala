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

package doodle.examples.svg

import cats.effect.unsafe.implicits.global
import doodle.examples.TextExamples
import doodle.svg.*
import doodle.syntax.all.*

import scala.scalajs.js.annotation.*

@JSExportTopLevel("SvgTextExamples")
object SvgTextExamples extends TextExamples[Algebra] {
  @JSExport
  def drawHello(id: String): Unit = hello.drawWithFrame(Frame(id))

  @JSExport
  def drawFont(id: String): Unit = font.drawWithFrame(Frame(id))

  @JSExport
  def drawDefault(id: String): Unit = default.drawWithFrame(Frame(id))

  @JSExport
  def drawFontFamily(id: String): Unit = fontFamily.drawWithFrame(Frame(id))
}
