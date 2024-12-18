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
import doodle.algebra.generic.*
import doodle.core.BoundingBox
import doodle.core.Transform as Tx
import doodle.core.font.Font

import java.awt.Graphics2D
import java.awt.geom.Rectangle2D

trait ReifiedText extends GenericText[Reification] {
  self: Algebra {
    type Drawing[A] <: doodle.java2d.Drawing[A]
    def gc: Graphics2D
  } =>

  val TextApi = new TextApi {
    type Bounds = Rectangle2D

    def text(
        tx: Tx,
        fill: Option[Fill],
        stroke: Option[Stroke],
        font: Font,
        text: String,
        bounds: Bounds
    ): Reification[Unit] =
      WriterT.tell(
        List(

          Reified.text(
            tx,
            fill,
            stroke,
            text,
            font,
            bounds
          )
        )
      )

    def textBoundingBox(text: String, font: Font): (BoundingBox, Bounds) = {
      val bounds = Java2D.textBounds(gc, text, font)

      (BoundingBox.centered(bounds.getWidth(), bounds.getHeight()), bounds)
    }
  }
}
