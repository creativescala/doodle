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

import cats.data.Kleisli
import cats.instances.all._
import cats.syntax.semigroup._
import javafx.geometry.Point2D

trait Layout extends doodle.algebra.Layout[Drawing,Unit] {
  def on(top: Drawing[Unit], bottom: Drawing[Unit]): Drawing[Unit] =
    for {
      t <- top
      b <- bottom
      (bbT, nextT) = t
      (bbB, nextB) = b
    } yield ((bbT on bbB), nextT |+| nextB)

  def beside(left: Drawing[Unit], right: Drawing[Unit]): Drawing[Unit] =
    for {
      l <- left
      r <- right
      (bbL, nextL) = l
      (bbR, nextR) = r
    } yield ((bbL beside bbR),
             Kleisli{ origin =>
               val leftOrigin = new Point2D(origin.getX() - bbL.right, origin.getY())
               val rightOrigin = new Point2D(origin.getX() - bbR.left, origin.getY())

               nextL.run(leftOrigin) |+| nextR.run(rightOrigin)
             })

  def above(top: Drawing[Unit], bottom: Drawing[Unit]): Drawing[Unit] =
    for {
      t <- top
      b <- bottom
      (bbT, nextT) = t
      (bbB, nextB) = b
    } yield ((bbT beside bbB),
             Kleisli{ origin =>
               val topOrigin = new Point2D(origin.getX(), origin.getY() - bbT.bottom)
               val bottomOrigin = new Point2D(origin.getX(), origin.getY() - bbB.top)

               nextT.run(topOrigin) |+| nextB.run(bottomOrigin)
             })
}
