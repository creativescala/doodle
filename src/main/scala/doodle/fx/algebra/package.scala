/*
 * Copyright 2015 noelwelsh
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
package fx

import cats.Eval
import cats.data.ReaderT
import cats.effect.IO
import doodle.algebra.DrawingContext
import doodle.layout.BoundingBox
import doodle.fx.engine.Transform
import javafx.geometry.Point2D
import javafx.scene.effect.BlendMode
import javafx.scene.paint.{Color => FxColor}
import javafx.scene.canvas.GraphicsContext

package object algebra {
  type FxContext = DrawingContext[BlendMode,FxColor]
  type Context = (GraphicsContext, FxContext, Transform.Transform)
  type Renderable[A] = (BoundingBox, ReaderT[IO,Point2D,A])
  type WithContext[A] = ReaderT[Eval, Context, Renderable[A]]
}
