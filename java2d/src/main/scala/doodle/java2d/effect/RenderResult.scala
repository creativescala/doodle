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

package doodle.java2d.effect

import doodle.core.BoundingBox
import doodle.java2d.algebra.reified.Reified

/** Event that is returned from Java2DPanel to represent result of rendering of
  * a Picture. This crosses the boundary between the Cats Effect and Swing
  * threading model.
  */
private[effect] final case class RenderResult[A](
    reified: List[Reified],
    boundingBox: BoundingBox,
    width: Double,
    height: Double,
    value: A
)
