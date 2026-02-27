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
package algebra
package generic

enum BlendMode {
  case Normal
  case Darken
  case Multiply
  case ColorBurn
  case Lighten
  case Screen
  case ColorDodge
  case Overlay
  case SoftLight
  case HardLight
  case Difference
  case Exclusion
  case Hue
  case Saturation
  case Color
  case Luminosity

  def toCssName: String =
    this match {
      case Normal     => "normal"
      case Darken     => "darken"
      case Multiply   => "multiply"
      case ColorBurn  => "color-burn"
      case Lighten    => "lighten"
      case Screen     => "screen"
      case ColorDodge => "color-dodge"
      case Overlay    => "overlay"
      case SoftLight  => "soft-light"
      case HardLight  => "hard-light"
      case Difference => "difference"
      case Exclusion  => "exclusion"
      case Hue        => "hue"
      case Saturation => "saturation"
      case Color      => "color"
      case Luminosity => "luminosity"
    }
}
