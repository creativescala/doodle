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
package core

import org.scalacheck.Prop.*
import org.scalacheck.*

object AngleSpec extends Properties("Angle properties") {
  import doodle.arbitrary.*
  import doodle.syntax.approximatelyEqual.*

  property("angle has bijection to Double as radians") = forAll { (a: Angle) =>
    a ~= Angle.radians(a.toRadians)
  }

  property("angle has bijection to Double as degrees") = forAll { (a: Angle) =>
    a ~= Angle.degrees(a.toDegrees)
  }

  property("angle has bijection to Double as turns") = forAll { (a: Angle) =>
    a ~= Angle.turns(a.toTurns)
  }

  property("angle negation is inverse") = forAll { (a: Angle) =>
    (a + (-a)) ~= Angle.zero
  }

  property("angle double negation is identity") = forAll { (a: Angle) =>
    a ~= -(-a)
  }
}
