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

import org.scalacheck._
import org.scalacheck.Prop._
import doodle.core._

object StyleSpec extends Properties("Style properties") {
  val style = new GenericStyle[TestGraphicsContext.Log,Unit] {}

  def impossible: Nothing = {
    println("Oops")
    throw new IllegalStateException("This case should never occur")
  }

  property("last fillColor takes effect") =
    forAll(Generators.finalized, Generators.color){ (f, c) =>
      val log = TestGraphicsContext.log()
      val (_, ctx) = style.fillColor(f, c)(DrawingContext.default)
      ctx((log, identity))(Point.zero).unsafeRunSync()

      val rendered = log.log
      rendered.foldLeft(true: Prop){ (prop, elt) =>
        prop && (elt.dc.fillColor.toOption.map(_ ?= c).getOrElse(exception))
      }
    }
}
