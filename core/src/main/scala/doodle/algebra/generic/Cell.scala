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
package algebra
package generic

/**
  * A cell is an immutable reference to an optional value, with possibly a
  * default value or an empty default.
  *
  * A default may be set (or unset) to a new value, but once set (or unset) that
  * value cannot change.
  */
sealed abstract class Cell[+A] extends Product with Serializable {
  import Cell._

  def set[AA >: A](value: AA): Cell[AA] =
    this match {
      case DefaultSet(_) => Set(value)
      case DefaultUnset  => Set(value)
      case Set(_) => this
      case Unset  => this
    }

  def unset: Cell[A] =
    this match {
      case DefaultSet(_) => Unset
      case DefaultUnset  => Unset
      case Set(_) => this
      case Unset  => this
    }

  def getOrElse[AA >: A](other: AA): AA =
    this match {
      case DefaultSet(a) => a
      case DefaultUnset  => other
      case Set(a) => a
      case Unset  => other
    }

  def foreach(f: A => Unit): Unit =
    this match {
      case DefaultSet(a) => f(a)
      case DefaultUnset  => ()
      case Set(a) => f(a)
      case Unset  => ()
    }

  def toOption: Option[A] =
    this match {
      case DefaultSet(a) => Some(a)
      case DefaultUnset  => None
      case Set(a) => Some(a)
      case Unset  => None
    }
}
object Cell {
  final case class DefaultSet[A](get: A) extends Cell[A]
  final case object DefaultUnset extends Cell[Nothing]
  final case class Set[A](get: A) extends Cell[A]
  final case object Unset extends Cell[Nothing]

  def default[A](value: A): Cell[A] = DefaultSet(value)
  def defaultUnset[A]: Cell[A] = DefaultUnset
  def set[A](value: A): Cell[A] = Set(value)
  def unset[A]: Cell[A] = Unset
}
