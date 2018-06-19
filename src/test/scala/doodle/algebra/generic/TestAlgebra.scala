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

import cats.Monoid
import cats.instances.all._

final case class TestAlgebra()
    extends GenericLayout[TestGraphicsContext.Log,Unit]
    with GenericPath[TestGraphicsContext.Log]
    with GenericShape[TestGraphicsContext.Log]
    with GenericStyle[TestGraphicsContext.Log,Unit] {
  implicit val monoid: Monoid[Unit] = implicitly[Monoid[Unit]]
  implicit val graphicsContext: GraphicsContext[TestGraphicsContext.Log] = TestGraphicsContext.logGraphicsContext
}
