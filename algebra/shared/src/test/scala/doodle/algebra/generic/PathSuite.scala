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

package doodle.algebra.generic

import doodle.algebra.Picture
import doodle.algebra.generic.reified.Reified
import doodle.algebra.generic.reified.Reified.StrokeClosedPath
import doodle.algebra.generic.reified.Reified.StrokeOpenPath
import doodle.core.ClosedPath
import doodle.core.Transform
import doodle.syntax.all.*
import munit.FunSuite

class PathSuite extends FunSuite {
  given TestAlgebra = TestAlgebra()

  def toReified(picture: Picture[TestAlgebra, Unit]): List[Reified] = {
    val (_, rdr) = picture.apply.run(List.empty).value
    val (_, fa) = rdr.run(Transform.identity).value
    val (reified, _) = fa.run.value

    reified
  }

  test("Converting a path to a picture produces the expected picture") {
    val closedPath = ClosedPath.roundedRectangle(100, 100, 7)
    val closedPicture = closedPath.toPicture

    val openPath = closedPath.open
    val openPicture = openPath.toPicture

    toReified(closedPicture) match {
      case StrokeClosedPath(_, _, elts) :: Nil =>
        assert(elts == closedPath.elements)

      case error =>
        fail(
          s"Closed path compiled into $error, which is not the expected reification"
        )
    }

    toReified(openPicture) match {
      case StrokeOpenPath(_, _, elts) :: Nil =>
        assert(elts == openPath.elements)

      case error =>
        fail(
          s"Open path compiled into $error, which is not the expected reification"
        )
    }
  }
}
