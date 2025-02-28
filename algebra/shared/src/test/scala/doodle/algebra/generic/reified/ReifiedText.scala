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
package reified

import cats.data.WriterT
import doodle.core.BoundingBox
import doodle.core.Transform as Tx
import doodle.core.font.Font

trait ReifiedText extends GenericText[Reification] {
  self: Algebra { type Drawing[A] = TestAlgebra.Drawing[A] } =>

  object TextApi extends TextApi {
    type Bounds = Unit

    def text(
        tx: Tx,
        fill: Option[Fill],
        stroke: Option[Stroke],
        font: Font,
        text: String,
        bounds: Bounds
    ): Reification[Unit] =
      WriterT.tell(List(Reified.text(tx, fill, stroke, font, text)))

    // We don't do proper layout for text in this test implementation
    def textBoundingBox(text: String, font: Font): (BoundingBox, Bounds) =
      (BoundingBox.centered(20, 20), ())
  }
}
