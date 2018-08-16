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
package language

import doodle.algebra._

trait Basic[F[_]] extends Layout[F] with Path[F] with Shape[F] with Style[F]
object Basic {
  def image[F[_],A](f: Basic[F] => F[A]): Image[Basic[F],F,A] =
    new Image[Basic[F],F,A] {
      def apply(implicit algebra: Basic[F]): F[A] =
        f(algebra)
    }
}
