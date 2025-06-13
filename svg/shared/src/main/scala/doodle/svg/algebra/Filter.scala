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
package svg
package algebra

import doodle.algebra.Filter
import doodle.algebra.generic.*
import doodle.core.Color

import cats.Eval

import scala.collection.mutable

import java.util.UUID

trait FilterModule { self: Base =>
    trait FilterImpl extends Filter {
        self: Algebra =>

        // @info does scalatags support SVG filters?
        // If not, we would need to implement the SVG filter elements ourselves.

        // @todo implement the filter methods using scalatags or similar
        // def gaussianBlur[A](picture: Drawing[A], stdDeviation: Double): Drawing[A] =
        //     picture.flatMap { (bb, rdr) => ... }

        // def boxBlur[A](picture: Drawing[A], radius: Int): Drawing[A] =
    }
}
