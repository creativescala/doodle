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

import doodle.syntax.all.*
import munit.CatsEffectSuite

class BlendSuite
    extends CatsEffectSuite
    with doodle.svg.algebra.TestAlgebraModule {

  test("screen blend should render with mix-blend-mode screen") {
    val picture = circle(100.0).screen

    Svg
      .renderWithoutRootTag(algebraInstance, picture)
      .map { case (_, tag, _) =>
        val tagStr = tag.render

        assert(tagStr.contains("mix-blend-mode: screen"))
      }
  }

  test("burn blend should render with mix-blend-mode color-burn") {
    val picture = circle(100.0).colorBurn

    Svg
      .renderWithoutRootTag(algebraInstance, picture)
      .map { case (_, tag, _) =>
        val tagStr = tag.render

        assert(tagStr.contains("mix-blend-mode: color-burn"))
      }
  }

  test("dodge blend should render with mix-blend-mode color-dodge") {
    val picture = circle(100.0).colorDodge

    Svg
      .renderWithoutRootTag(algebraInstance, picture)
      .map { case (_, tag, _) =>
        val tagStr = tag.render

        assert(tagStr.contains("mix-blend-mode: color-dodge"))
      }
  }

  test("lighten blend should render with mix-blend-mode lighten") {
    val picture = circle(100.0).lighten

    Svg
      .renderWithoutRootTag(algebraInstance, picture)
      .map { case (_, tag, _) =>
        val tagStr = tag.render

        assert(tagStr.contains("mix-blend-mode: lighten"))
      }
  }

  test("sourceOver blend should render with mix-blend-mode normal") {
    val picture = circle(100.0).normal

    Svg
      .renderWithoutRootTag(algebraInstance, picture)
      .map { case (_, tag, _) =>
        val tagStr = tag.render

        assert(tagStr.contains("mix-blend-mode: normal"))
      }
  }
}
