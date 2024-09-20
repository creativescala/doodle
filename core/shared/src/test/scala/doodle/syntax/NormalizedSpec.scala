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
package syntax

import doodle.core.Normalized
import org.scalacheck.*
import org.scalacheck.Prop.*

class NormalizedSpec extends Properties("Normalized syntax") {
  import doodle.syntax.normalized.*

  property(".normalized") = forAll { (d: Double) =>
    if d >= 1.0 then d.normalized ?= Normalized.MaxValue
    else if d <= 0.0 then d.normalized ?= Normalized.MinValue
    else d.normalized ?= Normalized.clip(d)
  }
}
