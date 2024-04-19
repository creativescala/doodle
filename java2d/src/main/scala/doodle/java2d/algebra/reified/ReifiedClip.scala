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
package java2d
package algebra
package reified

import cats.data.WriterT
import doodle.algebra.generic._
import doodle.core.BoundingBox
import doodle.core.ClosedPath
import doodle.core.{Transform => Tx}

import java.awt.Graphics2D
import java.awt.geom.Rectangle2D

trait ReifiedClip extends GenericClip[Reification] {
  self: Algebra {
    type Drawing[A] <: doodle.java2d.Drawing[A]
    def gc: Graphics2D
  } =>

  val ClipApi = new ClipApi {
    type Bounds = Rectangle2D

    def clip[Unit](
        tx: Tx,
        img: Drawing[Unit],
        clipPath: ClosedPath
    ): Reification[Unit] = ???
      // WriterT.tell(List(Reified.clip(tx,img, clipPath)))
  }
}
