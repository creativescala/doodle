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

import cats.Semigroup
import cats.syntax.all._
import doodle.core.{Point,Vec}

trait GenericLayout[G] extends Layout[Finalized[G,?]] {
  def on[A: Semigroup](top: Finalized[G,A], bottom: Finalized[G,A]): Finalized[G,A] =
    for {
      t <- top
      b <- bottom
      (bbT, ctxT) = t
      (bbB, ctxB) = b
    } yield ((bbT on bbB), ctxT |+| ctxB)

  def beside[A: Semigroup](left: Finalized[G,A], right: Finalized[G,A]): Finalized[G,A] =
    for {
      l <- left
      r <- right
      (bbL, ctxL) = l
      (bbR, ctxR) = r
    } yield ((bbL beside bbR),
             Contextualized{ ctx =>
               val rdrL = ctxL(ctx)
               val rdrR = ctxR(ctx)

               Renderable { origin =>
                 val leftOrigin = Point(origin.x - bbL.right, origin.y)
                 val rightOrigin = Point(origin.x - bbR.left, origin.y)
                 rdrL(leftOrigin) |+| rdrR(rightOrigin)
               }
             })

  def above[A: Semigroup](top: Finalized[G,A], bottom: Finalized[G,A]): Finalized[G,A] =
    for {
      t <- top
      b <- bottom
      (bbT, ctxT) = t
      (bbB, ctxB) = b
    } yield ((bbT beside bbB),
             Contextualized{ ctx =>
               val rdrT = ctxT(ctx)
               val rdrB = ctxB(ctx)

               Renderable { origin =>
                 val topOrigin = Point(origin.x, origin.y - bbT.bottom)
                 val bottomOrigin = Point(origin.x, origin.y - bbB.top)

                 rdrT(topOrigin) |+| rdrB(bottomOrigin)
               }
             })

  def at[A](img: Finalized[G,A], x: Double, y: Double): Finalized[G,A] =
    img.map{ case (bb, ctx) =>
      (bb.at(x, y),
       Contextualized{ c =>
         val rdr = ctx(c)

         Renderable{ origin =>
           rdr(origin + Vec(x, y))
         }
       })
    }

}
