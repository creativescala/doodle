/*
 * Copyright 2015-2020 Noel Welsh
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

sealed trait Gradient extends Product with Serializable
object Gradient {
  final case class Linear(
      start: Point,
      end: Point,
      stops: Seq[(Color, Double)],
      cycleMethod: CycleMethod
  ) extends Gradient
  final case class Radial(
      outer: Point,
      inner: Point,
      radius: Double,
      stops: Seq[(Color, Double)],
      cycleMethod: CycleMethod
  ) extends Gradient

  def linear(
      start: Point,
      end: Point,
      stops: Seq[(Color, Double)],
      cycleMethod: CycleMethod
  ): Gradient =
    Linear(start, end, stops, cycleMethod)

  def radial(
      outer: Point,
      inner: Point,
      radius: Double,
      stops: Seq[(Color, Double)],
      cycleMethod: CycleMethod
  ): Gradient =
    Radial(outer, inner, radius, stops, cycleMethod)

  def dichromaticVertical(color1: Color, color2: Color, length: Double) =
    Linear(
      Point.zero,
      Point.Cartesian(0, length),
      List((color1, 0.0), (color2, 1.0)),
      CycleMethod.repeat
    )

  def dichromaticHorizontal(color1: Color, color2: Color, length: Double) =
    Linear(
      Point.zero,
      Point.Cartesian(length, 0),
      List((color1, 0.0), (color2, 1.0)),
      CycleMethod.repeat
    )

  def dichromaticRadial(color1: Color, color2: Color, radius: Double) =
    Radial(
      Point.zero,
      Point.zero,
      radius,
      List((color1, 0.0), (color2, 1.0)),
      CycleMethod.repeat
    )

  sealed trait CycleMethod extends Product with Serializable {}
  object CycleMethod {
    case object NoCycle extends CycleMethod
    case object Reflect extends CycleMethod
    case object Repeat extends CycleMethod

    val noCycle: CycleMethod = NoCycle
    val reflect: CycleMethod = Reflect
    val repeat: CycleMethod = Repeat
  }
}
