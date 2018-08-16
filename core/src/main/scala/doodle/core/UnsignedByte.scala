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

final case class UnsignedByte(value: Byte) extends AnyVal {
  def +(that: UnsignedByte): Int =
    this.value + that.value

  def -(that: UnsignedByte): Int =
    this.value - that.value

  def get: Int =
    (value + 128)

  def toNormalized: Normalized =
    Normalized.clip(get.toDouble / UnsignedByte.MaxValue.get.toDouble)

}
object UnsignedByte {
  val MinValue = UnsignedByte(-128.toByte)
  val MaxValue = UnsignedByte(127.toByte)

  def clip(value: Int): UnsignedByte =
    value match {
      case _ if value < 0 => MinValue
      case _ if value > 255 => MaxValue
      case v => UnsignedByte((v - 128).toByte)
    }
}
