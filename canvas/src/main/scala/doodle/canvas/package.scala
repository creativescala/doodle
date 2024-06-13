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

import doodle.algebra._
import doodle.effect.Renderer

type Algebra = doodle.canvas.algebra.CanvasAlgebra
type Canvas = doodle.canvas.effect.Canvas
type Drawing[A] =
  doodle.algebra.generic.Finalized[doodle.canvas.algebra.CanvasDrawing, A]

type Frame = doodle.canvas.effect.Frame
val Frame = doodle.canvas.effect.Frame

given Renderer[Algebra, Frame, Canvas] = doodle.canvas.effect.CanvasRenderer

type Picture[A] = doodle.algebra.Picture[Algebra, A]
object Picture extends BaseConstructor, PathConstructor, ShapeConstructor {

  type Algebra = doodle.canvas.Algebra
  type Drawing[A] = doodle.canvas.Drawing[A]
}
