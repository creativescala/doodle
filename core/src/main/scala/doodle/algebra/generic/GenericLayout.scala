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
import cats.implicits._

trait GenericLayout[G] extends Layout[Finalized[G, ?]] {
  import Renderable._
  def on[A: Semigroup](top: Finalized[G, A],
                       bottom: Finalized[G, A]): Finalized[G, A] =
    for {
      t <- top
      b <- bottom
      (bbT, rdrT) = t
      (bbB, rdrB) = b
    } yield ((bbT on bbB), rdrB |+| rdrT)

  def beside[A: Semigroup](left: Finalized[G, A],
                           right: Finalized[G, A]): Finalized[G, A] =
    for {
      l <- left
      r <- right
      (bbL, rdrL) = l
      (bbR, rdrR) = r
    } yield {
      val bb = bbL.beside(bbR)
      (bb, Renderable.make { tx =>
        val txLeft = tx.translate(bb.left - bbL.left, 0)
        val txRight = tx.translate(bb.right - bbR.right, 0)

        rdrL.run(txLeft) |+| rdrR.run(txRight)
      })
      // Contextualized{ ctx =>
      //   val rdrL = ctxL(ctx)
      //   val rdrR = ctxR(ctx)

      //   Renderable { origin =>
      //     val leftOrigin = Point(origin.x + bb.left - bbL.left, origin.y)
      //     val rightOrigin = Point(origin.x + bb.right - bbR.right, origin.y)
      //     rdrL(leftOrigin) |+| rdrR(rightOrigin)
      //   }
      // })
    }

  def above[A: Semigroup](top: Finalized[G, A],
                          bottom: Finalized[G, A]): Finalized[G, A] =
    for {
      t <- top
      b <- bottom
      (bbT, rdrT) = t
      (bbB, rdrB) = b
    } yield {
      val bb = bbT.above(bbB)
      (bb, Renderable.make { tx =>
         val txTop = tx.translate(0, bb.top - bbT.top)
         val txBottom = tx.translate(0, bb.bottom - bbB.bottom)

         rdrT.run(txTop) |+| rdrB.run(txBottom)
       })
      // Contextualized{ ctx =>
      //   val rdrT = ctxT(ctx)
      //   val rdrB = ctxB(ctx)

      //   Renderable { origin =>
      //     val topOrigin = Point(origin.x, origin.y + bb.top - bbT.top)
      //     val bottomOrigin = Point(origin.x, origin.y + bb.bottom - bbB.bottom)

      //     rdrT(topOrigin) |+| rdrB(bottomOrigin)
      //   }
      // })
    }

  def at[A](img: Finalized[G, A], x: Double, y: Double): Finalized[G, A] =
    img.map {
      case (bb, rdr) =>
        (bb.at(x, y), Renderable.make { tx =>
          rdr.run(tx.translate(x, y))
        // val rdr = ctx(c)

        // Renderable{ origin =>
        //   rdr(origin + Vec(x, y))
        // }
        })
    }
}
