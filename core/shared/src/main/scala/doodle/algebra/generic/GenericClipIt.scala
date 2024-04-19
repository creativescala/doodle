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

import cats.data.State
import doodle.core.BoundingBox
import doodle.core.ClosedPath
import doodle.core.{Transform => Tx}

trait GenericClip[G[_]] extends Clip {
  self: Algebra { type Drawing[A] = Finalized[G, A] } =>

  trait ClipApi {
    type Bounds

    def clip[A](
        tx: Tx,
        img: Drawing[A],
        clipPath: ClosedPath
    ): G[A]
  }

  def ClipApi: ClipApi

  def clip[A](img: Drawing[A], clipPath: ClosedPath): Drawing[A] =
    Finalized.leaf {dc =>
      val strokeWidth = dc.strokeWidth.getOrElse(0.0)
      val bb = BoundingBox.centered(strokeWidth + 100, strokeWidth + 100)
      (
        bb,
        State.inspect(tx =>
          ClipApi.clip(tx, img, clipPath)
        )
      )
    }
}
