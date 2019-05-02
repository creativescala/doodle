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
package java2d
package algebra

import doodle.language.Basic
import doodle.algebra.generic._
import doodle.algebra.generic.reified._

final case class Algebra()
  extends ReifiedLayout
  with ReifiedPath
  with ReifiedShape
  with GenericStyle[Reification]
  with GenericTransform[Reification]
  with Basic[Drawing]
