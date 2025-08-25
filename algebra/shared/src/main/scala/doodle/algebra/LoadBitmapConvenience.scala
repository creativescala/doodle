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

package doodle.algebra

/** Convenience methods for LoadBitmap that provide synchronous, unsafe
  * operations.
  *
  * These are convenience methods for cases where:
  *   - You don't want to deal with IO ceremony
  *   - You're in a context where throwing exceptions is acceptable
  *   - You need simple fallback behavior for errors
  *
  * Note: The actual implementation is platform-specific due to differences in
  * how IO is executed synchronously or asynchronously on JVM vs JS.
  */
trait LoadBitmapConvenience {

  /** Load a bitmap synchronously, throwing any errors that occur.
    *
    * This is the simplest but most dangerous way to load a bitmap. Use when
    * you're certain the bitmap exists and is valid.
    *
    * @param specifier
    *   the location/identifier of the bitmap
    * @param loader
    *   implicit LoadBitmap instance for the types
    * @return
    *   the loaded bitmap
    * @throws BitmapError
    *   if loading fails
    */
  def loadUnsafe[S, B](specifier: S)(using loader: LoadBitmap[S, B]): B

  /** Load a bitmap synchronously, returning a default value on any error.
    *
    * This is safer than loadUnsafe as it won't throw exceptions. Useful when
    * you have a reasonable fallback image.
    *
    * @param specifier
    *   the location/identifier of the bitmap
    * @param default
    *   the bitmap to return if loading fails (evaluated lazily)
    * @param loader
    *   implicit LoadBitmap instance for the types
    * @return
    *   the loaded bitmap or the default
    */
  def loadOrDefault[S, B](specifier: S, default: => B)(using
      loader: LoadBitmap[S, B]
  ): B

  /** Load a bitmap synchronously with partial error recovery.
    *
    * Allows handling specific error cases while letting others propagate.
    * Useful when different errors require different fallback strategies.
    *
    * @param specifier
    *   the location/identifier of the bitmap
    * @param recover
    *   partial function to handle specific BitmapError cases
    * @param loader
    *   implicit LoadBitmap instance for the types
    * @return
    *   the loaded bitmap or recovery result
    * @throws Throwable
    *   if the error is not handled by recover
    */
  def loadOrRecover[S, B](specifier: S)(
      recover: PartialFunction[BitmapError, B]
  )(using loader: LoadBitmap[S, B]): B
}
