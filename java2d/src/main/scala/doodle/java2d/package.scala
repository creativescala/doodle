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

import doodle.algebra.generic.reified.Reification
import doodle.effect.DefaultRenderer
import doodle.explore.effect.ExplorerFactory
import doodle.interact.effect.Animator
import doodle.language.Basic
import javax.swing.JComponent

package object java2d extends effect.Java2dExplorerAtoms {
  type Algebra[F[_]] = doodle.algebra.Algebra[F] with Basic[F]
  type Drawing[A] = doodle.algebra.generic.Finalized[Reification, A]
  type Renderable[A] = doodle.algebra.generic.Renderable[Reification, A]

  type Canvas = doodle.java2d.effect.Canvas
  implicit val java2dCanvasAlgebra = doodle.java2d.algebra.CanvasAlgebra

  implicit val java2dAnimator: Animator[Canvas] =
    doodle.java2d.effect.Java2dAnimator

  // Magnolia doesn't work if I just define
  //   def gen[A] = Java2dExplorer.gen[A]
  // Hence it's defined inline here, which pollutes the namespace
  import magnolia._
  type Typeclass[A] = effect.Java2dExplorer.Typeclass[A]
  def combine[A](caseClass: CaseClass[Typeclass, A]): Typeclass[A] =
    effect.Java2dExplorer.combine(caseClass)
  def dispatch[A](sealedTrait: SealedTrait[Typeclass, A]): Typeclass[A] =
    effect.Java2dExplorer.dispatch(sealedTrait)
  implicit def java2dExplorerFactory[A]: ExplorerFactory[JComponent, A] =
    macro Magnolia.gen[A]

  implicit val java2dRenderer
    : DefaultRenderer[Algebra, Drawing, doodle.java2d.effect.Frame, Canvas] =
    doodle.java2d.effect.Java2dRenderer
  implicit val java2dGifWriter = doodle.java2d.effect.Java2dGifWriter
  implicit val java2dPngWriter = doodle.java2d.effect.Java2dPngWriter
  implicit val java2dJpgWriter = doodle.java2d.effect.Java2dJpgWriter

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
