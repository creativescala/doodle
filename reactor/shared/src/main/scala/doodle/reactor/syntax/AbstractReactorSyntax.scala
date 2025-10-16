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

package doodle.reactor.syntax

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import doodle.effect.DefaultFrame
import doodle.effect.Renderer
import doodle.interact.algebra.MouseClick
import doodle.interact.algebra.MouseMove
import doodle.interact.effect.AnimationRenderer
import doodle.interact.syntax.all.*
import doodle.language.Basic
import doodle.reactor.BaseReactor

trait AbstractReactorSyntax {

  /** Subtypes should implement this with unsafeRunSync or unsafeRunAsync as
    * appropriate. Returns Unit because unsafeRunAsync cannot return a value.
    */
  protected def runIO[A](io: IO[A])(implicit runtime: IORuntime): Unit

  extension [A](reactor: BaseReactor[A]) {
    def animate[Alg <: Basic, Frame, Canvas]()(using
        a: AnimationRenderer[Canvas],
        d: DefaultFrame[Frame],
        e: Renderer[Alg, Frame, Canvas],
        m: MouseClick[Canvas] & MouseMove[Canvas],
        r: IORuntime
    ): Unit =
      animateWithFrame(d.default)

    def animateWithFrame[Alg <: Basic, Frame, Canvas](frame: Frame)(using
        a: AnimationRenderer[Canvas],
        e: Renderer[Alg, Frame, Canvas],
        m: MouseClick[Canvas] & MouseMove[Canvas],
        r: IORuntime
    ): Unit =
      runIO(animateWithFrameToIO(frame))

    def animateToIO[Alg <: Basic, Frame, Canvas]()(using
        a: AnimationRenderer[Canvas],
        d: DefaultFrame[Frame],
        e: Renderer[Alg, Frame, Canvas],
        m: MouseClick[Canvas] & MouseMove[Canvas],
        r: IORuntime
    ): IO[Unit] =
      animateWithFrameToIO(d.default)

    def animateWithFrameToIO[Alg <: Basic, Frame, Canvas](frame: Frame)(using
        a: AnimationRenderer[Canvas],
        e: Renderer[Alg, Frame, Canvas],
        m: MouseClick[Canvas] & MouseMove[Canvas],
        r: IORuntime
    ): IO[Unit] =
      e.canvas(frame).use(c => animateWithCanvasToIO(c))

    def animateWithCanvasToIO[Alg <: Basic, Frame, Canvas](canvas: Canvas)(using
        a: AnimationRenderer[Canvas],
        e: Renderer[Alg, Frame, Canvas],
        m: MouseClick[Canvas] & MouseMove[Canvas],
        r: IORuntime
    ): IO[Unit] =
      reactor.build(canvas).animateWithCanvasToIO(canvas)
  }
}
