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

package doodle.canvas

import doodle.algebra.*
import doodle.canvas.algebra.CanvasLoadBitmap
import doodle.canvas.algebra.CanvasToPicture
import doodle.effect.Renderer
import doodle.interact.algebra.MouseClick
import doodle.interact.algebra.MouseMove
import doodle.interact.algebra.Redraw
import doodle.interact.effect.AnimationRenderer
import org.scalajs.dom

type Algebra = doodle.canvas.algebra.CanvasAlgebra
type Canvas = doodle.canvas.effect.Canvas
type Drawing[A] =
  doodle.algebra.generic.Finalized[doodle.canvas.algebra.CanvasDrawing, A]

type Frame = doodle.canvas.effect.Frame
val Frame = doodle.canvas.effect.Frame

given LoadBitmap[String, dom.HTMLImageElement] =
  CanvasLoadBitmap.loadBitmapFromUrl
given LoadBitmap[String, dom.ImageBitmap] =
  CanvasLoadBitmap.loadBitMapFromUrlToImageBitmap

given ToPicture[dom.HTMLImageElement, Algebra] =
  CanvasToPicture.HTMLImageElementToPicture
given ToPicture[dom.ImageBitmap, Algebra] =
  CanvasToPicture.ImageBitmapToPicture

given (MouseClick[Canvas] & MouseMove[Canvas] & Redraw[Canvas]) =
  doodle.canvas.effect.CanvasAlgebra

given Renderer[Algebra, Frame, Canvas] = doodle.canvas.effect.CanvasRenderer
given AnimationRenderer[Canvas] =
  doodle.canvas.effect.CanvasAnimationRenderer

type Picture[A] = doodle.algebra.Picture[Algebra, A]
object Picture
    extends BaseConstructor,
      PathConstructor,
      ShapeConstructor,
      TextConstructor {

  type Algebra = doodle.canvas.Algebra
  type Drawing[A] = doodle.canvas.Drawing[A]
}
