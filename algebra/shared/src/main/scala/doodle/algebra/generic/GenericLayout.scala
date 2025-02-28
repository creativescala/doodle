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

import cats.*
import cats.implicits.*
import doodle.core.BoundingBox
import doodle.core.Landmark
import doodle.core.Transform

trait GenericLayout[G[_]] extends Layout {
  self: GivenApply[G] with Algebra { type Drawing[A] = Finalized[G, A] } =>
  import Renderable.*

  def on[A](top: Finalized[G, A], bottom: Finalized[G, A])(implicit
      s: Semigroup[A]
  ): Finalized[G, A] =
    Finalized { ctxTxs =>
      val t = top.run(ctxTxs)
      val b = bottom.run(ctxTxs)

      (t, b).mapN { (t, b) =>
        val (bbT, rdrT) = t
        val (bbB, rdrB) = b
        ((bbT.on(bbB)), rdrB |+| rdrT)
      }
    }

  def beside[A](left: Finalized[G, A], right: Finalized[G, A])(implicit
      s: Semigroup[A]
  ): Finalized[G, A] =
    Finalized { ctxTxs =>
      val l = left.run(ctxTxs)
      val r = right.run(ctxTxs)

      (l, r).mapN { (l, r) =>
        val (bbL, rdrL) = l
        val (bbR, rdrR) = r
        val bb = bbL.beside(bbR)

        val txLeft = Transform.translate(bb.left - bbL.left, 0)
        val txRight = Transform.translate(bb.right - bbR.right, 0)
        val rdr = Renderable.parallel(txLeft, txRight)(rdrL)(rdrR)

        (bb, rdr)
      }
    }

  def above[A](top: Finalized[G, A], bottom: Finalized[G, A])(implicit
      s: Semigroup[A]
  ): Finalized[G, A] =
    Finalized { ctxTxs =>
      val t = top.run(ctxTxs)
      val b = bottom.run(ctxTxs)

      (t, b).mapN { (t, b) =>
        val (bbT, rdrT) = t
        val (bbB, rdrB) = b
        val bb = bbT.above(bbB)

        val txTop = Transform.translate(0, bb.top - bbT.top)
        val txBottom = Transform.translate(0, bb.bottom - bbB.bottom)
        val rdr = Renderable.parallel(txTop, txBottom)(rdrT)(rdrB)

        (bb, rdr)
      }
    }

  def at[A](img: Finalized[G, A], landmark: Landmark): Finalized[G, A] =
    img.map { case (bb, rdr) =>
      val point = bb.eval(landmark)
      (
        bb.at(point),
        Renderable.transform(Transform.translate(point.x, point.y))(rdr)
      )
    }

  def originAt[A](img: Finalized[G, A], landmark: Landmark): Finalized[G, A] =
    img.map { case (bb, rdr) =>
      val point = bb.eval(landmark)
      // Moving the origin to point p is equivalent to translating the image to -p
      (
        bb.originAt(landmark),
        Renderable.transform(Transform.translate(-point.x, -point.y))(rdr)
      )
    }

  def margin[A](
      img: Finalized[G, A],
      top: Double,
      right: Double,
      bottom: Double,
      left: Double
  ): Finalized[G, A] =
    img.map { case (bb, rdr) =>
      val newBb = BoundingBox(
        left = bb.left - left,
        top = bb.top + top,
        right = bb.right + right,
        bottom = bb.bottom - bottom
      )
      (newBb, rdr)
    }

  def size[A](
      img: Finalized[G, A],
      width: Double,
      height: Double
  ): Finalized[G, A] = {
    assert(
      width >= 0,
      s"Called `size` with a width of ${width}. The bounding box's width must be non-negative."
    )
    assert(
      height >= 0,
      s"Called `size` with a height of ${height}. The bounding box's height must be non-negative."
    )
    val w = width / 2.0
    val h = height / 2.0

    img.map { case (bb, rdr) =>
      val newBb = BoundingBox(
        left = -w,
        top = h,
        right = w,
        bottom = -h
      )

      (newBb, rdr)
    }
  }
}
