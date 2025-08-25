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

package doodle.syntax

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import doodle.algebra.BitmapError
import doodle.algebra.LoadBitmap

import scala.concurrent.Future

trait LoadBitmapConvenienceSyntax {

  /** Extension methods for the LoadBitmap companion object */
  extension (companion: LoadBitmap.type) {

    /** Load a bitmap asynchronously.
      *
      * @param specifier
      *   the URL of the bitmap
      * @param loader
      *   implicit LoadBitmap instance for the types
      * @param runtime
      *   explicit IORuntime for executing the IO
      * @return
      *   Future containing the loaded bitmap or failure
      */
    def loadUnsafeAsync[S, B](specifier: S)(using
        loader: LoadBitmap[S, B],
        runtime: IORuntime
    ): Future[B] = {
      loader.load(specifier).unsafeToFuture()
    }

    /** Load a bitmap asynchronously, returning a default value on any error.
      *
      * @param specifier
      *   the URL of the bitmap
      * @param default
      *   the bitmap to return if loading fails
      * @param loader
      *   implicit LoadBitmap instance for the types
      * @param runtime
      *   explicit IORuntime for executing the IO
      * @return
      *   Future containing the loaded bitmap or the default
      */
    def loadOrDefaultAsync[S, B](specifier: S, default: => B)(using
        loader: LoadBitmap[S, B],
        runtime: IORuntime
    ): Future[B] = {
      loader
        .load(specifier)
        .attempt
        .unsafeToFuture()
        .map {
          case Left(_)       => default
          case Right(bitmap) => bitmap
        }(scala.concurrent.ExecutionContext.global)
    }

    /** Load a bitmap asynchronously with partial error recovery.
      *
      * @param specifier
      *   the URL of the bitmap
      * @param recover
      *   partial function to handle specific BitmapError cases
      * @param loader
      *   implicit LoadBitmap instance for the types
      * @param runtime
      *   explicit IORuntime for executing the IO
      * @return
      *   Future containing the loaded bitmap or recovery result
      */
    def loadOrRecoverAsync[S, B](specifier: S)(
        recover: PartialFunction[BitmapError, B]
    )(using
        loader: LoadBitmap[S, B],
        runtime: IORuntime
    ): Future[B] = {
      loader
        .load(specifier)
        .handleErrorWith { error =>
          error match {
            case be: BitmapError if recover.isDefinedAt(be) =>
              IO.pure(recover(be))
            case other =>
              IO.raiseError(other)
          }
        }
        .unsafeToFuture()
    }
  }
}
