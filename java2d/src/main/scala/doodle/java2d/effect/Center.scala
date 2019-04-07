/*
 * Copyright 2019 Creative Scala
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

/** Determines where the center of the canvas is located. */
sealed abstract class Center extends Product with Serializable
object Center {

  // Algebraic data type members
  final case object CenteredOnImage extends Center
  final case object AtOrigin extends Center

  // Smart constructors

  /** The center of the canvas is centered on the image. */
  val centeredOnImage: Center = CenteredOnImage
  /** The center of the canvas is the origin. */
  val atOrigin: Center = AtOrigin
}
