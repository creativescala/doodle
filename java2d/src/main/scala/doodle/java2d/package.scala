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

import doodle.algebra.{Bitmap, Text}
import doodle.effect.DefaultRenderer
import doodle.interact.effect.AnimationRenderer
import doodle.java2d.algebra.reified.Reification
import doodle.language.Basic
import javax.swing.JComponent
import doodle.algebra.ToPicture
import java.awt.image.BufferedImage
import doodle.effect.Base64
import doodle.effect.Writer
import doodle.effect.Writer._
import doodle.interact.effect.AnimationWriter
import doodle.interact.algebra._
import doodle.core.{Base64 => B64}

package object java2d {
  type Algebra[F[_]] =
    doodle.algebra.Algebra[F] with Basic[F] with Bitmap[F] with Text[F]
  type Drawing[A] = doodle.algebra.generic.Finalized[Reification, A]
  type Renderable[A] = doodle.algebra.generic.Renderable[Reification, A]

  type Frame = doodle.java2d.effect.Frame
  type Canvas = doodle.java2d.effect.Canvas
  implicit val java2dCanvasAlgebra: MouseClick[Canvas] with MouseMove[Canvas] with Redraw[Canvas] =
    doodle.java2d.algebra.CanvasAlgebra

  implicit val java2dAnimationRenderer: AnimationRenderer[Canvas] =
    doodle.java2d.effect.Java2dAnimationRenderer
  implicit val java2dGifAnimationWriter: AnimationWriter[Algebra, Drawing, Frame, Gif] =
    doodle.java2d.effect.Java2dAnimationWriter

  implicit val java2dRenderer
    : DefaultRenderer[Algebra, Drawing, doodle.java2d.effect.Frame, Canvas] =
    doodle.java2d.effect.Java2dRenderer
  implicit val java2dGifWriter: Writer[Algebra, Drawing, Frame, Gif] with Base64[Algebra, Drawing, Frame, Gif] = doodle.java2d.effect.Java2dGifWriter
  implicit val java2dPngWriter: Writer[Algebra, Drawing, Frame, Png] with Base64[Algebra, Drawing, Frame, Png] = doodle.java2d.effect.Java2dPngWriter
  implicit val java2dJpgWriter: Writer[Algebra, Drawing, Frame, Jpg] with Base64[Algebra, Drawing, Frame, Jpg] = doodle.java2d.effect.Java2dJpgWriter
  implicit val java2dPdfWriter: Writer[Algebra, Drawing, Frame, Pdf] with Base64[Algebra, Drawing, Frame, Pdf] = doodle.java2d.effect.Java2dPdfWriter

  implicit val java2dBufferedImageToPicture: ToPicture[Drawing, BufferedImage] =
    doodle.java2d.algebra.reified.BufferedImageToPicture
  implicit val java2dBase64PngToPicture: ToPicture[Drawing, B64[Png]] =
    doodle.java2d.algebra.reified.Base64PngToPicture
  implicit val java2dBase64GifToPicture: ToPicture[Drawing, B64[Gif]] =
    doodle.java2d.algebra.reified.Base64GifToPicture
  implicit val java2dBase64JpgToPicture: ToPicture[Drawing, B64[Jpg]] =
    doodle.java2d.algebra.reified.Base64JpgToPicture

  val Frame = doodle.java2d.effect.Frame

  type Picture[A] = doodle.algebra.Picture[Algebra, Drawing, A]
  object Picture {
    def apply(f: Algebra[Drawing] => Drawing[Unit]): Picture[Unit] = {
      new Picture[Unit] {
        def apply(implicit algebra: Algebra[Drawing]): Drawing[Unit] =
          f(algebra)
      }
    }
  }
}
