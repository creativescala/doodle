/*
 * Copyright 2015 noelwelsh
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

import scala.annotation.tailrec

/**
  * A value in the range [0, 1]
  */
final case class Normalized(get: Double) extends AnyVal {
  def +(that: Normalized): Double =
    this.get + that.get

  def -(that: Normalized): Double =
    this.get - that.get

  def max(that: Normalized): Normalized =
    if(this.get > that.get)
      this
    else
      that

  def min(that: Normalized): Normalized =
    if(this.get < that.get)
      this
    else
      that

  def toTurns: Angle =
    Angle.turns(get)

  def toUnsignedByte: UnsignedByte =
    UnsignedByte.clip(Math.round(get * UnsignedByte.MaxValue.get).toInt)
}

object Normalized {
  val MinValue = Normalized(0.0)
  val MaxValue = Normalized(1.0)

  def clip(value: Double): Normalized =
    value match {
      case _ if value < 0.0 => MinValue
      case _ if value > 1.0 => MaxValue
      case v => Normalized(v)
    }

  def wrap(value: Double): Normalized = {
    @tailrec def loop(value: Double): Normalized = value match {
      case v if v > 1.0 => loop(v - 1.0)
      case v if v < 0.0 => loop(v + 1.0)
      case v => Normalized(v)
    }

    loop(value)
  }
}

