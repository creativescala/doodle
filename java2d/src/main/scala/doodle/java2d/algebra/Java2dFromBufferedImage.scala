/*
 * Copyright 2015 Noel Welsh
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

package doodle.java2d.algebra

import cats.Eval
import cats.data.State
import cats.data.WriterT
import doodle.algebra.FromBufferedImage
import doodle.algebra.generic.Finalized
import doodle.core.BoundingBox
import doodle.core.Transform
import doodle.java2d.Drawing
import doodle.java2d.algebra.reified.Reified

import java.awt.image.BufferedImage

trait Java2dFromBufferedImage extends FromBufferedImage {
  self: Algebra { type Drawing[A] = doodle.java2d.Drawing[A] } =>

  def fromBufferedImage(in: BufferedImage): Drawing[Unit] =
    Finalized.leaf { _ =>
      val w = in.getWidth()
      val h = in.getHeight()
      val bb = BoundingBox.centered(w.toDouble, h.toDouble)
      (
        bb,
        State.inspect { (tx: Transform) =>
          WriterT.tell[Eval, List[Reified]](List(Reified.bitmap(tx, in)))
        }
      )
    }
}
