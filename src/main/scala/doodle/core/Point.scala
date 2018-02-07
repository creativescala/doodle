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

sealed abstract class Point extends Product with Serializable {
  import Point._

  def -(that: Point): Vec =
    Vec(this.x - that.x, this.y - that.y)

  def +(vec: Vec): Point = {
    val cartesian = this.toCartesian
    Cartesian(cartesian.x + vec.x, cartesian.y + vec.y)
  }

  def x: Double
  def y: Double
  def r: Double
  def angle: Angle

  def rotate(angle: Angle): Point = {
    val polar = this.toPolar
    Polar(polar.r, polar.angle + angle)
  }

  def lengthen(r: Double): Point = {
    val polar = this.toPolar
    Polar(polar.r + r, polar.angle)
  }

  def toVec: Vec = {
    val cartesian = this.toCartesian
    Vec(cartesian.x, cartesian.y)
  }

  def toCartesian: Cartesian =
    this match {
      case c @ Cartesian(_, _) => c
      case Polar(r, a) =>
        Cartesian(r * a.cos, r * a.sin)
    }

  def toPolar: Polar =
    this match {
      case Cartesian(x, y) =>
        val r = math.sqrt(x*x + y*y)
        val angle = Angle.radians(math.atan2(y, x))
        Polar(r, angle)
      case p @ Polar(_, _) =>
        p
    }
}
object Point {
  final case class Cartesian(x: Double, y: Double) extends Point {
    def r: Double =
      this.toPolar.r

    def angle: Angle =
      this.toPolar.angle
  }
  final case class Polar(r: Double, angle: Angle) extends Point {
    def x: Double =
      this.toCartesian.x

    def y: Double =
      this.toCartesian.y
  }

  val zero: Point = Cartesian(0,0)

  def apply(x: Double, y: Double) =
    cartesian(x, y)

  def apply(r: Double, angle: Angle) =
    polar(r, angle)

  def polar(r: Double, angle: Angle): Point =
    Polar(r, angle)

  def cartesian(x: Double, y: Double): Point =
    Cartesian(x, y)

  // This provides extractors / unapply methods that work in a slightly unusual
  // way. that work is a slightly unusual way. As we can freely convert between
  // the two representations, and thus there is no need to stop someone viewing
  // a Point represented as, say, a Cartesian, as, say, a Polar.
  //
  // You can use these extractors when you have a point and you want to view it
  // in a particular way (e.g. a Polar) and you don't care how it is actually
  // implemented---it will be converted for you if it isn't currently using the
  // representation you want.
  object extractors {
    object Cartesian {
      def unapply(pt: Point): Option[(Double, Double)] = {
        val cartesian = pt.toCartesian
        Some((cartesian.x, cartesian.y))
      }
    }
    object Polar {
      def unapply(pt: Point): Option[(Double, Angle)] = {
        val polar = pt.toPolar
        Some((polar.r, polar.angle))
      }
    }
  }
}
