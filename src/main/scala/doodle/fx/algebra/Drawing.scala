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
package algebra

import cats.Eval
import cats.data.{Kleisli,ReaderT}
import cats.effect.IO
import doodle.core.Point
import doodle.layout.BoundingBox
import javafx.scene.canvas.GraphicsContext
import doodle.fx.engine.Transform.Transform

object Drawing {
  def now[A](f: (GraphicsContext, FxContext, Transform) => (BoundingBox, ReaderT[IO,Point,A])): Drawing[A] =
    Kleisli{ (context: Context) =>
      val (gc, dc, tx) = context
      Eval.now(f(gc, dc, tx))
    }

  def now[A](f: (GraphicsContext, FxContext) => (BoundingBox, ReaderT[IO,Point,A])): Drawing[A] =
    Kleisli{ (context: Context) =>
      val (gc, dc, _) = context
      Eval.now(f(gc, dc))
    }

  def contextTransform[A](f: FxContext => FxContext)(around: Drawing[A]): Drawing[A] =
    Kleisli{ (context: Context) =>
      val (gc, dc, tx) = context
      val newDc = f(dc)
      around.run((gc, newDc, tx))
    }
}
