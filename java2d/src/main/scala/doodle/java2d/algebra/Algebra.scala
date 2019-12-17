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
 * WITHOUT WARRANTIES OR CONDITIONS OReification ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package doodle
package java2d
package algebra

import cats._
import cats.implicits._
import doodle.language.Basic
import doodle.algebra.generic._
import doodle.java2d.algebra.reified._
import java.awt.Graphics2D

final case class Algebra(
    gc: Graphics2D,
    applyF: Apply[Reification] = Apply.apply[Reification],
    functorF: Functor[Reification] = Apply.apply[Reification]
) extends Basic[Drawing]
    with ReifiedBitmap
    with ReifiedPath
    with ReifiedShape
    with ReifiedText
    with GenericDebug[Reification]
    with GenericLayout[Reification]
    with GenericSize[Reification]
    with GenericStyle[Reification]
    with GenericTransform[Reification]
    with GivenApply[Reification]
    with GivenFunctor[Reification]
