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

import cats.Eval
import cats.data.State
import cats.data.WriterT
import doodle.algebra._
import doodle.algebra.generic._
import doodle.core.BoundingBox
import doodle.core.Transform
import doodle.core.format._
import doodle.java2d.algebra.reified._

import java.io.ByteArrayInputStream
import java.util.{Base64 => JBase64}
import javax.imageio.ImageIO

trait Java2dFromBase64
    extends FromPngBase64
    with FromGifBase64
    with FromJpgBase64 {
  self: doodle.algebra.Algebra { type Drawing[A] = doodle.java2d.Drawing[A] } =>

  def fromGifBase64(base64: core.Base64[Gif]): Drawing[Unit] =
    genericFromBase64(base64.value)

  def fromPngBase64(base64: core.Base64[Png]): Drawing[Unit] = 
    genericFromBase64(base64.value)

  def fromJpgBase64(base64: core.Base64[Jpg]): Drawing[Unit] =
    genericFromBase64(base64.value)

  def genericFromBase64(value: String): Drawing[Unit] =
    Finalized.leaf { _ =>
      val bytes = JBase64.getDecoder().decode(value)
      val bs = new ByteArrayInputStream(bytes)
      val bi = ImageIO.read(bs)
      val w = bi.getWidth()
      val h = bi.getHeight()
      val bb = BoundingBox.centered(w.toDouble, h.toDouble)
      (
        bb,
        State.inspect { (tx: Transform) =>
          WriterT.tell[Eval, List[Reified]](List(Reified.bitmap(tx, bi)))
        }
      )
    }
}
