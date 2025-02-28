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

package doodle.core.syntax

import doodle.core.Angle
import org.scalacheck.*
import org.scalacheck.Prop.*

class AngleSpec extends Properties("Angle syntax properties") {
  import doodle.core.syntax.all.*

  property(".degrees") = forAll { (d: Double) => d.degrees ?= Angle.degrees(d) }

  property(".radians") = forAll { (d: Double) => d.radians ?= Angle.radians(d) }

  property(".turns") = forAll { (d: Double) => d.turns ?= Angle.turns(d) }
}
