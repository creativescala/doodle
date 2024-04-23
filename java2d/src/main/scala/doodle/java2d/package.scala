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

import doodle.algebra._
import doodle.core.format._
import doodle.effect.Base64Writer
import doodle.effect.BufferedImageWriter
import doodle.effect.DefaultFrame
import doodle.effect.FileWriter
import doodle.effect.Renderer
import doodle.interact.algebra._
import doodle.interact.effect.AnimationRenderer
import doodle.interact.effect.AnimationWriter
import doodle.java2d.algebra.reified.Reification
import doodle.language.Basic

package object java2d extends Java2dToPicture {
  type Algebra =
    doodle.algebra.Algebra
      with Basic
      with Bitmap
      with Clip
      with FromBufferedImage
      with FromPngBase64
      with FromGifBase64
      with FromJpgBase64
  type Drawing[A] = doodle.algebra.generic.Finalized[Reification, A]
  type Renderable[A] = doodle.algebra.generic.Renderable[Reification, A]

  type Frame = doodle.java2d.effect.Frame
  type Canvas = doodle.java2d.effect.Canvas
  implicit val java2dCanvasAlgebra
      : MouseClick[Canvas] with MouseMove[Canvas] with Redraw[Canvas] =
    doodle.java2d.algebra.CanvasAlgebra

  implicit val java2dAnimationRenderer: AnimationRenderer[Canvas] =
    doodle.java2d.effect.Java2dAnimationRenderer
  implicit val java2dGifAnimationWriter: AnimationWriter[Algebra, Frame, Gif] =
    doodle.java2d.effect.Java2dAnimationWriter

  implicit val java2dRenderer
      : Renderer[Algebra, doodle.java2d.effect.Frame, Canvas] =
    doodle.java2d.effect.Java2dRenderer
  implicit val java2dFrame: DefaultFrame[doodle.java2d.effect.Frame] =
    doodle.java2d.effect.Java2dDefaultFrame
  implicit val java2dGifWriter
      : FileWriter[Algebra, Frame, Gif] with Base64Writer[Algebra, Frame, Gif] =
    doodle.java2d.effect.Java2dGifWriter
  implicit val java2dPngWriter
      : FileWriter[Algebra, Frame, Png] with Base64Writer[Algebra, Frame, Png] =
    doodle.java2d.effect.Java2dPngWriter
  implicit val java2dJpgWriter
      : FileWriter[Algebra, Frame, Jpg] with Base64Writer[Algebra, Frame, Jpg] =
    doodle.java2d.effect.Java2dJpgWriter
  implicit val java2dPdfWriter
      : FileWriter[Algebra, Frame, Pdf] with Base64Writer[Algebra, Frame, Pdf] =
    doodle.java2d.effect.Java2dPdfWriter
  implicit val java2dBufferedImageWriter
      : BufferedImageWriter[doodle.java2d.Algebra, Frame] =
    doodle.java2d.effect.Java2dBufferedImageWriter

  val Frame = doodle.java2d.effect.Frame

  type Picture[A] = doodle.algebra.Picture[Algebra, A]
  object Picture
      extends BaseConstructor
      with BitmapConstructor
      with ClipConstructor
      with FromGifBase64Constructor
      with FromPngBase64Constructor
      with FromJpgBase64Constructor
      with PathConstructor
      with ShapeConstructor
      with TextConstructor {

    type Algebra = java2d.Algebra
    type Drawing[A] = java2d.Drawing[A]
  }
}
