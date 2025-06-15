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

import doodle.core.*
import doodle.syntax.all.*
import munit.CatsEffectSuite

class FilterSpec
    extends CatsEffectSuite
    with doodle.svg.algebra.TestAlgebraModule {

  test("gaussianBlur filter should render") {
    val picture = circle(100.0).blur(5.0)

    Svg
      .renderWithoutRootTag(algebraInstance, picture)
      .map { case (_, tag, _) =>
        val tagStr = tag.render

        assert(tagStr.contains("filter"))
        assert(tagStr.contains("feGaussianBlur"))
        assert(tagStr.contains("stdDeviation=\"5\""))
      }
  }

  test("boxBlur filter should render") {
    val picture = circle(100.0).boxBlur(3)

    Svg
      .renderWithoutRootTag(algebraInstance, picture)
      .map { case (_, tag, _) =>
        val tagStr = tag.render

        assert(tagStr.contains("filter"))
        assert(tagStr.contains("feConvolveMatrix"))
      }
  }

  test("detectEdges filter should render") {
    val picture = circle(100.0).detectEdges

    Svg
      .renderWithoutRootTag(algebraInstance, picture)
      .map { case (_, tag, _) =>
        val tagStr = tag.render

        assert(tagStr.contains("filter"))
        assert(tagStr.contains("feConvolveMatrix"))
        assert(tagStr.contains("kernelMatrix"))
      }
  }

  test("sharpen filter should render") {
    val picture = circle(100.0).sharpen(2.0)

    Svg
      .renderWithoutRootTag(algebraInstance, picture)
      .map { case (_, tag, _) =>
        val tagStr = tag.render

        assert(tagStr.contains("filter"))
        assert(tagStr.contains("feConvolveMatrix"))
      }
  }

  test("emboss filter should render") {
    val picture = circle(100.0).emboss

    Svg
      .renderWithoutRootTag(algebraInstance, picture)
      .map { case (_, tag, _) =>
        val tagStr = tag.render

        assert(tagStr.contains("filter"))
        assert(tagStr.contains("feConvolveMatrix"))
      }
  }

  test("dropShadow filter should render") {
    val picture = circle(100.0).dropShadow(4.0, 4.0, 2.0, Color.black)

    Svg
      .renderWithoutRootTag(algebraInstance, picture)
      .map { case (_, tag, _) =>
        val tagStr = tag.render

        assert(tagStr.contains("filter"))
        assert(tagStr.contains("feGaussianBlur"))
        assert(tagStr.contains("feOffset"))
        assert(tagStr.contains("feMerge"))
        assert(tagStr.contains("stdDeviation=\"2\""))
        assert(tagStr.contains("dx=\"4\""))
        assert(tagStr.contains("dy=\"4\""))
      }
  }

  test("custom convolve filter should render") {
    val kernel = Vector(
      Vector(0.0, -1.0, 0.0),
      Vector(-1.0, 5.0, -1.0),
      Vector(0.0, -1.0, 0.0)
    )
    val picture = circle(100.0).convolve(kernel)

    Svg
      .renderWithoutRootTag(algebraInstance, picture)
      .map { case (_, tag, _) =>
        val tagStr = tag.render

        assert(tagStr.contains("filter"))
        assert(tagStr.contains("feConvolveMatrix"))
        assert(tagStr.contains("order=\"3\""))
      }
  }

  test("filters should generate unique IDs") {
    val picture1 = circle(50.0).blur(2.0)
    val picture2 = circle(50.0).blur(2.0)

    for {
      result1 <- Svg.renderWithoutRootTag(algebraInstance, picture1)
      result2 <- Svg.renderWithoutRootTag(algebraInstance, picture2)
    } yield {
      val (_, tag1, _) = result1
      val (_, tag2, _) = result2

      assertNotEquals(tag1.toString, tag2.toString)
    }
  }

  test("multiple filters can be applied") {
    val picture = circle(100.0).blur(2.0).dropShadow(2.0, 2.0, 1.0)

    Svg
      .renderWithoutRootTag(algebraInstance, picture)
      .map { case (_, tag, _) =>
        val tagStr = tag.render

        assert(tagStr.contains("filter"))
        assert(tagStr.contains("feGaussianBlur"))
        assert(tagStr.contains("feOffset"))
      }
  }
}
