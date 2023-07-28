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
package algebra
package generic

import cats.Eval
import cats.Later
import cats.data.*
import doodle.core.BoundingBox
import doodle.core.{Transform => Tx}

/** A [[Finalized]] represents an effect that, when run, produces all the
  * information needed to layout an image (it "finalizes" all the information
  * needed for this process) and can eventually produce a value of type `A`
  * (once it is rendered). Algorithmically this means:
  *
  *   - for each shape work out its [[DrawingContext]] from which we can work
  *     out a [[doodle.core.BoundingBox]].
  *
  *   - apply transforms to bounding boxes at the point they are defined so each
  *     transformed subtree is laid out in its local coordinate system.
  *
  * The List of ContextTransform's are supplied in the order they should be
  * applied: the innermost transform is at the head of the list.
  */
final case class Finalized[F[_], A](
    f: Transforms => Eval[(BoundingBox, Renderable[F, A])]
) {
  def boundingBox: BoundingBox = {
    val (bb, _) = run(List.empty).value
    bb
  }

  def map[B](
      f: (BoundingBox, Renderable[F, A]) => (BoundingBox, Renderable[F, B])
  ): Finalized[F, B] =
    Finalized(ctxTxs => run(ctxTxs).map((bb, rdr) => f(bb, rdr)))

  def flatMap[B](
      f: (BoundingBox, Renderable[F, A]) => Finalized[F, B]
  ): Finalized[F, B] =
    Finalized(ctxTxs =>
      run(ctxTxs).flatMap((bb, rdr) => f(bb, rdr).run(ctxTxs))
    )

  def run(
      ctxTxs: List[ContextTransform]
  ): Eval[(BoundingBox, Renderable[F, A])] =
    f(ctxTxs)
}
object Finalized {
  def applyU[F[_], A](
      f: Transforms => (BoundingBox, Renderable[F, A])
  ): Finalized[F, A] =
    Finalized(ctxTxs => Later(f(ctxTxs)))

  /** Create a leaf [[Finalized]]. It will be passed a [[DrawingContext]] with
    * all transforms applied in the correct order.
    */
  def leaf[F[_], A](
      f: DrawingContext => (BoundingBox, Renderable[F, A])
  ): Finalized[F, A] =
    Finalized.applyU { ctxTxs =>
      val dc = ctxTxs.foldLeft(DrawingContext.default)((dc, f) => f(dc))
      f(dc)
    }

  def contextTransform[F[_], A](
      f: DrawingContext => DrawingContext
  )(child: Finalized[F, A]): Finalized[F, A] =
    Finalized(ctxTxs => child.run(f :: ctxTxs))

  def transform[F[_], A](transform: Tx)(
      child: Finalized[F, A]
  ): Finalized[F, A] =
    Finalized { ctxTxs =>
      child
        .run(ctxTxs)
        .map((bb, rdr) =>
          (bb.transform(transform), rdr.contramap(tx => transform.andThen(tx)))
        )
    }
}
