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
package algebra
package generic

import doodle.core.Transform as Tx

trait GenericTransform[G[_]] extends Transform {
  self: Algebra { type Drawing[A] = Finalized[G, A] } =>

  def transform[A](img: Finalized[G, A], tx: Tx): Finalized[G, A] =
    Finalized.transform(tx)(img)
}
