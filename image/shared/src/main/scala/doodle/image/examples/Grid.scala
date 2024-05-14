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
package image
package examples

import cats.implicits.*
import doodle.image.syntax.all.*
import doodle.random.*

/** Grid patterns in the style of Sasj's geometric series such as
  * https://www.instagram.com/p/BohZM4RiWJk/?taken-by=sasj_nl
  */
object Grid {
  def row(size: Int)(image: Random[Image]): Random[Image] =
    (0 until size).map(_ => image).toList.sequence.map(_.allBeside)

  def square(size: Int)(image: Random[Image]): Random[Image] =
    (0 until size).map(_ => row(size)(image)).toList.sequence.map(_.allAbove)
}
