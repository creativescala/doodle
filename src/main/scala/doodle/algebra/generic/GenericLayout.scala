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

import cats.{Monad,Monoid}
import cats.data.{Kleisli,ReaderT}
import cats.effect.IO
import cats.syntax.all._
import doodle.core.Point
import doodle.layout.BoundingBox

trait GenericLayout[F[_],A] extends Layout[({ type L[T] = F[(BoundingBox, ReaderT[IO,Point,T])]})#L,A] {
  implicit val monad: Monad[F]
  implicit val monoid: Monoid[A]

  type Drawing = F[(BoundingBox, ReaderT[IO,Point,A])]

  def on(top: Drawing, bottom: Drawing): Drawing =
    for {
      t <- top
      b <- bottom
      (bbT, nextT) = t
      (bbB, nextB) = b
    } yield ((bbT on bbB), nextT |+| nextB)

  def beside(left: Drawing, right: Drawing): Drawing =
    for {
      l <- left
      r <- right
      (bbL, nextL) = l
      (bbR, nextR) = r
    } yield ((bbL beside bbR),
             Kleisli{ origin =>
               val leftOrigin = Point(origin.x - bbL.right, origin.y)
               val rightOrigin = Point(origin.x - bbR.left, origin.y)

               nextL.run(leftOrigin) |+| nextR.run(rightOrigin)
             })

  def above(top: Drawing, bottom: Drawing): Drawing =
    for {
      t <- top
      b <- bottom
      (bbT, nextT) = t
      (bbB, nextB) = b
    } yield ((bbT beside bbB),
             Kleisli{ origin =>
               val topOrigin = Point(origin.x, origin.y - bbT.bottom)
               val bottomOrigin = Point(origin.x, origin.y - bbB.top)

               nextT.run(topOrigin) |+| nextB.run(bottomOrigin)
             })
}
