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

import cats.data.State
import doodle.core.{BoundingBox, Transform => Tx}
import doodle.core.font.Font

trait GenericText[F[_]] extends Text[Finalized[F, ?]] {

  trait TextApi {
    def text(tx: Tx, font: Font, text: String): F[Unit]
    def textBoundingBox(text: String, font: Font): BoundingBox
  }

  def TextApi: TextApi

  def font[A](image: Finalized[F, A], font: Font): Finalized[F, A] =
    Finalized.contextTransform(_.font(font))(image)

  def text(text: String): Finalized[F, Unit] =
    Finalized.leaf { dc =>
      val bb = TextApi.textBoundingBox(text, dc.font)
      (bb, State.inspect(tx => TextApi.text(tx, dc.font, text)))
    }
}
