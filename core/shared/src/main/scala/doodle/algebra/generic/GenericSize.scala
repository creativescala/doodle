/*
 * Copyright 2019 Noel Welsh
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

import cats.Functor
import cats.syntax.functor._

/**
  * Get information about the size of the bounding box enclosing an image.
  */
class GenericSize[F[_]]()(implicit val functorF: Functor[F]) extends Size[Finalized[F, ?]] {
  /**
   * Get the height of the bounding box enclosing the image
   */
  def height[A](image: Finalized[F, A]): Finalized[F, Double] =
    image.map{ case (bb, rdr) =>
      (bb, rdr.map(fa => fa.map(_ => bb.height)))
    }

  /**
   * Get the width of the bounding box enclosing the image
   */
  def width[A](image: Finalized[F, A]): Finalized[F, Double] =
    image.map{ case (bb, rdr) =>
      (bb, rdr.map(fa => fa.map(_ => bb.width)))
    }

  /**
   * Get the width and height of the bounding box enclosing the image
   */
  def size[A](image: Finalized[F, A]): Finalized[F, (Double,Double)] =
    image.map{ case (bb, rdr) =>
      (bb, rdr.map(fa => fa.map(_ => (bb.width, bb.height))))
    }
}
