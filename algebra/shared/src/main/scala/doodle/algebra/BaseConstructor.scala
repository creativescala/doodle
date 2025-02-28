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

/** Base module for constructors
  *
  * The intention is to assist with type inference for constructors by defining
  * constructors that are parameterized by the Algebra and Drawing types, and
  * instantiating those types within each backend.
  *
  * Algebras that define constructors should also define a constructor mixin.
  * See e.g. Shape for an example.
  */
trait BaseConstructor {
  type Algebra <: doodle.algebra.Algebra
  type Drawing[A]

  type Picture[A] = doodle.algebra.Picture[Algebra, A]
}
