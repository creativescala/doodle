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
package algebra
package generic

import doodle.core.BoundingBox

trait Instances {
  implicit val boundingBoxDistance: Distance[BoundingBox] =
    Distance((bb1, bb2) =>
      Math.abs(bb1.left - bb2.left) +
        Math.abs(bb1.right - bb2.right) +
        Math.abs(bb1.top - bb2.top) +
        Math.abs(bb1.bottom - bb2.bottom)
    )
}
object Instances extends Instances
