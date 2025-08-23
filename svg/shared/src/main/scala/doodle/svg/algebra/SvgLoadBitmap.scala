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
package svg
package algebra

import cats.effect.IO
import doodle.algebra.LoadBitmap

/** SVG-specific image reference that stores URL and optional dimensions */
final case class SvgImageRef(
    href: String,
    width: Option[Double] = None,
    height: Option[Double] = None
)

/** LoadBitmap implementation for SVG that creates image references */
object SvgLoadBitmap {
  val loadBitmapFromUrl: LoadBitmap[String, SvgImageRef] =
    new LoadBitmap[String, SvgImageRef] {
      def load(specifier: String): IO[SvgImageRef] =
        IO.pure(SvgImageRef(specifier))
    }

  /** Create an image reference with specific dimensions */
  def withDimensions(
      href: String,
      width: Double,
      height: Double
  ): IO[SvgImageRef] =
    IO.pure(SvgImageRef(href, Some(width), Some(height)))

  /** Create an image reference with width only (height will be auto-calculated)
    */
  def withWidth(href: String, width: Double): IO[SvgImageRef] =
    IO.pure(SvgImageRef(href, Some(width), None))

  /** Create an image reference with height only (width will be auto-calculated)
    */
  def withHeight(href: String, height: Double): IO[SvgImageRef] =
    IO.pure(SvgImageRef(href, None, Some(height)))
}
