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
package core

import doodle.core.Color.Oklch
import doodle.core.Color.Rgb
import doodle.core.syntax.all.*
import org.scalacheck.Gen

trait Generators {
  val angle: Gen[Angle] =
    Gen.choose(-360, 360).map(_.degrees)

  val point: Gen[Point] =
    for {
      x <- Gen.choose(-1000.0, 1000.0)
      y <- Gen.choose(-1000.0, 1000.0)
    } yield Point.cartesian(x, y)

  val normalized: Gen[Normalized] =
    Gen.choose(0.0, 1.0).map(Normalized.clip)

  val unsignedByte: Gen[UnsignedByte] =
    Gen.choose(0, 255).map(UnsignedByte.clip _)

  val oklch: Gen[Oklch] =
    for {
      l <- normalized
      c <- Gen.choose(0.0, 0.4)
      h <- angle
      a <- normalized
    } yield Color.Oklch(l, c, h, a)

  val color: Gen[Color] = oklch

  val rgb: Gen[Rgb] =
    for {
      r <- unsignedByte
      g <- unsignedByte
      b <- unsignedByte
      a <- normalized
    } yield Rgb(r, g, b, a)

  val pathElement: Gen[PathElement] = {
    val point: Gen[Point] =
      for {
        x <- Gen.choose(-100.0, 100.0)
        y <- Gen.choose(-100.0, 100.0)
      } yield Point(x, y)

    val moveTo: Gen[PathElement] =
      point.map(pt => PathElement.moveTo(pt))

    val lineTo: Gen[PathElement] =
      point.map(PathElement.lineTo _)

    val bezierCurveTo: Gen[PathElement] =
      for {
        cp1 <- point
        cp2 <- point
        to <- point
      } yield PathElement.curveTo(cp1, cp2, to)

    Gen.oneOf(moveTo, lineTo, bezierCurveTo)
  }
}
object Generators extends Generators
