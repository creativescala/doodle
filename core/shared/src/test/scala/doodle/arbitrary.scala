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

import org.scalacheck.Arbitrary
import org.scalacheck.Gen

object arbitrary {
  import doodle.core.*
  import doodle.core.Color.{Rgb, Oklch}

  final case class Translate(x: Double, y: Double)
  final case class Scale(x: Double, y: Double)
  final case class Rotate(angle: Angle)

  /** The dimensions of a screen: positive integers greater than 0 and less than
    * or equal to 4000.
    */
  final case class Screen(width: Int, height: Int)

  val genScreen: Gen[Screen] =
    for {
      w <- Gen.choose(1, 4000)
      h <- Gen.choose(1, 4000)
    } yield Screen(w, h)

  implicit val arbitraryPoint: Arbitrary[Point] =
    Arbitrary(Generators.point)

  implicit val arbitraryAngle: Arbitrary[Angle] =
    Arbitrary(Generators.angle)

  implicit val arbitraryScale: Arbitrary[Scale] =
    Arbitrary(Generators.point.map(pt => Scale(pt.x, pt.y)))

  implicit val arbitraryRotate: Arbitrary[Rotate] =
    Arbitrary(Generators.angle.map(d => Rotate(d)))

  implicit val arbitraryTranslate: Arbitrary[Translate] =
    Arbitrary(Generators.point.map(pt => Translate(pt.x, pt.y)))

  implicit val arbitraryScreen: Arbitrary[Screen] =
    Arbitrary(genScreen)

  implicit val arbitraryOklch: Arbitrary[Oklch] =
    Arbitrary(Generators.oklch)

  implicit val arbitraryRgb: Arbitrary[Rgb] =
    Arbitrary(Generators.rgb)
}
