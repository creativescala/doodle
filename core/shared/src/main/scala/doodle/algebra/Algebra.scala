/*
 * Copyright 2015-2020 Noel Welsh
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

import cats.Applicative

/** Base type for algebras that produce results in some effect type `Drawing`.
  * Users of algebras should use dependent method types (or dependent function
  * types in Scala 3) to return the `Drawing` type of the method they are
  * passed:
  *
  * ```scala
  * def usingAlgebra(algebra: Algebra): algebra.Drawing = ???
  * ```
  *
  * All `Drawing` types are required to implement `Applicative`
  */
trait Algebra {

  /** The effect type that methods on this algebra produce. Represents an effect
    * that, when run, will draw something and produce a value.
    */
  type Drawing[_]
  implicit val drawingInstance: Applicative[Drawing]
}
object Algebra {
  type Aux[F0[_]] = Algebra { type Drawing[A] = F0[A] }
}
