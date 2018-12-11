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
package algebra
package generic

import cats._
import cats.data.IndexedStateT
import cats.implicits._
import doodle.core.Transform

trait GenericLayout[G] extends Layout[Finalized[G, ?]] {
  import Renderable._
  def on[A: Semigroup](top: Finalized[G, A],
                       bottom: Finalized[G, A]): Finalized[G, A] =
    IndexedStateT { ctxTxs =>
      val t = top.runA(ctxTxs)
      val b = bottom.runA(ctxTxs)

      (t, b).mapN { (t, b) =>
        val (bbT, rdrT) = t
        val (bbB, rdrB) = b
        (ctxTxs, ((bbT.on(bbB)), rdrB |+| rdrT))
      }
    }

  def beside[A: Semigroup](left: Finalized[G, A],
                           right: Finalized[G, A]): Finalized[G, A] =
    IndexedStateT { ctxTxs =>
      val l = left.runA(ctxTxs)
      val r = right.runA(ctxTxs)

      (l, r).mapN { (l, r) =>
        val (bbL, rdrL) = l
        val (bbR, rdrR) = r
        val bb = bbL.beside(bbR)

        val txLeft = Transform.translate(bb.left - bbL.left, 0)
        val txRight = Transform.translate(bb.right - bbR.right, 0)
        val rdr = Renderable.parallel(txLeft, txRight)(rdrL)(rdrR)

        (ctxTxs, (bb, rdr))
      }
    }

  def above[A: Semigroup](top: Finalized[G, A],
                          bottom: Finalized[G, A]): Finalized[G, A] =
    IndexedStateT { ctxTxs =>
      val t = top.runA(ctxTxs)
      val b = bottom.runA(ctxTxs)

      (t, b).mapN { (t, b) =>
        val (bbT, rdrT) = t
        val (bbB, rdrB) = b
        val bb = bbT.above(bbB)

        val txTop = Transform.translate(0, bb.top - bbT.top)
        val txBottom = Transform.translate(0, bb.bottom - bbB.bottom)
        val rdr = Renderable.parallel(txTop, txBottom)(rdrT)(rdrB)

        (ctxTxs, (bb, rdr))
      }
    }

  def at[A](img: Finalized[G, A], x: Double, y: Double): Finalized[G, A] =
    img.map {
      case (bb, rdr) =>
        (bb.at(x, y), Renderable.transform(Transform.translate(x, y))(rdr))
    }
  // img.map {
  //   case (bb, rdr) =>
  //     (bb.at(x, y), Renderable.make { tx =>
  //       rdr.run(Transform.translate(x, y).andThen(tx))
  // val rdr = ctx(c)

  // Renderable{ origin =>
  //   rdr(origin + Vec(x, y))
  // }
  // })
  // }
}
