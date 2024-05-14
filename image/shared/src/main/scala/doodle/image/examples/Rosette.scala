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

import cats.implicits.*
import doodle.core.*
import doodle.image.syntax.all.*
import doodle.syntax.all.*

object Rosette {
  val pts = List(
    Point(300, 0.degrees),
    // Point(300, 10.degrees),
    Point(300, 20.degrees),
    // Point(300, 30.degrees),
    Point(300, 40.degrees),
    // Point(300, 50.degrees),
    Point(300, 60.degrees),
    // Point(300, 70.degrees),
    Point(300, 80.degrees),
    // Point(300, 90.degrees),
    Point(300, 100.degrees),
    // Point(300, 110.degrees),
    Point(300, 120.degrees),
    // Point(300, 130.degrees),
    Point(300, 140.degrees),
    // Point(300, 150.degrees),
    Point(300, 160.degrees),
    // Point(300, 170.degrees),
    Point(300, 180.degrees),
    // Point(300, 190.degrees),
    Point(300, 200.degrees),
    // Point(300, 210.degrees),
    Point(300, 220.degrees),
    // Point(300, 230.degrees),
    Point(300, 240.degrees),
    // Point(300, 250.degrees),
    Point(300, 260.degrees),
    // Point(300, 270.degrees),
    Point(300, 280.degrees),
    // Point(300, 290.degrees),
    Point(300, 300.degrees),
    // Point(300, 310.degrees),
    Point(300, 320.degrees),
    // Point(300, 330.degrees),
    Point(300, 340.degrees)
    // Point(300, 350.degrees),
  )

  val circles: Image =
    pts.map(pt => Image.circle(10).at(pt.toVec)).allOn

  def mapTails[A, B](list: List[A])(f: (A, A) => B): List[B] =
    list match {
      case Nil => Nil
      case x :: xs =>
        xs.map(f(x, _)) ::: mapTails(xs)(f)
    }

  val lines: Image =
    mapTails(pts) { (pt1, pt2) =>
      Image.path(
        OpenPath.empty.moveTo(pt1).lineTo(pt2)
      ): Image
    }.allOn

  val image = circles.on(lines)
}
