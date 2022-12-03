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
package image
package examples

import doodle.core._
import doodle.syntax.angle._
import doodle.syntax.normalized._

object ChristmasTree {
  import Color._

  val redBauble = Image.circle(7) strokeWidth 0 fillColor red
  val goldBauble = Image.circle(10) strokeWidth 0 fillColor gold

  def treeElement = {
    val color = green
      .spin((math.random() * 30).degrees)
      .darken((math.random() * 0.1).normalized)
      .desaturate((math.random() * 0.1).normalized)

    Image.triangle(40, 40) strokeWidth 0 fillColor color
  }

  def row(elements: Int): Image =
    elements match {
      case 1 => redBauble on treeElement
      case n => redBauble on treeElement beside row(n - 1)
    }

  def tree(levels: Int): Image =
    levels match {
      case 1 => treeElement
      case n => row(n) below tree(n - 1)
    }

  val levels = 4
  val foliage = Image.triangle(40.0 * levels, 40.0 * levels) fillColor darkGreen
  val trunk = Image.rectangle(20.0, 40.0) strokeColor brown fillColor brown

  val image = goldBauble above (tree(levels) on foliage) above trunk
}
