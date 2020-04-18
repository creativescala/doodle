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
package java2d
package effect

import doodle.core.Color

/**
 * Determines how the [[Canvas]] handles drawing multiple
 * [[doodle.algebra.Picture]]s. The default is to clear the [[Canvas]] with the
 * [[Frame]]'s background color every time a new picture is rendered.
 * Alternatively a different color can be specified. This allows one to blend
 * together pictures while keeping a constant background.
 */
sealed trait Redraw
object Redraw {
  case object ClearToBackground extends Redraw
  final case class ClearToColor(color: Color) extends Redraw

  /**
   * Clear the [[Canvas]] with the [[Frame]]'s background color when rendering a
   * new [[doodle.algebra.Picture]].
   */
  val clearToBackground: Redraw = ClearToBackground

  /**
   * Clear the [[Canvas]] with the given color when rendering a new
   * [[doodle.algebra.Picture]].
   */
  def clearToColor(color: Color): Redraw =
    ClearToColor(color)
}
