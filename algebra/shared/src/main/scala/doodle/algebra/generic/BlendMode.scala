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

sealed abstract class BlendMode extends Product with Serializable
object BlendMode {
  case object Screen extends BlendMode
  case object Burn extends BlendMode
  case object Dodge extends BlendMode
  case object Lighten extends BlendMode
  case object SourceOver extends BlendMode

  val screen: BlendMode = Screen
  val burn: BlendMode = Burn
  val dodge: BlendMode = Dodge
  val lighten: BlendMode = Lighten
  val sourceOver: BlendMode = SourceOver
}
