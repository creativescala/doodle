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
package java2d
package algebra
package reified

import cats.Eval
import cats.data.{State, WriterT}
import doodle.algebra.ToPicture
import doodle.algebra.generic._
import doodle.core.{Base64, BoundingBox, Transform}
import doodle.effect.Writer._
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.util.{Base64 => JBase64}
import javax.imageio.ImageIO

trait BaseToPicture[Input] extends ToPicture[Drawing, Input] {
  def toPicture(in: Input): Drawing[Unit]
}

object BufferedImageToPicture extends BaseToPicture[BufferedImage] {
  def toPicture(in: BufferedImage): Drawing[Unit] = {
    Finalized.leaf{ _ =>
      val w = in.getWidth()
      val h = in.getHeight()
      val bb = BoundingBox.centered(w.toDouble, h.toDouble)
      (bb,
       State.inspect{ (tx: Transform) =>
         WriterT.tell[Eval, List[Reified]](List(Reified.bitmap(tx, in)))
       })
    }
  }
}

trait GenericBase64ToPicture[A] extends BaseToPicture[Base64[A]] {
  def toPicture(in: Base64[A]): Drawing[Unit] = {
    Finalized.leaf{ _ =>
      val bytes = JBase64.getDecoder().decode(in.value)
      val bs = new ByteArrayInputStream(bytes)
      val bi = ImageIO.read(bs)
      val w = bi.getWidth()
      val h = bi.getHeight()
      val bb = BoundingBox.centered(w.toDouble, h.toDouble)
      (bb,
       State.inspect{ (tx: Transform) =>
         WriterT.tell[Eval, List[Reified]](List(Reified.bitmap(tx, bi)))
       })
    }

  }
}
object Base64PngToPicture extends GenericBase64ToPicture[Png]
object Base64GifToPicture extends GenericBase64ToPicture[Gif]
object Base64JpgToPicture extends GenericBase64ToPicture[Jpg]
