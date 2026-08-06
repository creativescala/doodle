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
package effect

import doodle.algebra.generic.*
import doodle.core.*
import doodle.language.Basic
import munit.CatsEffectSuite

import scala.collection.mutable

class SvgSpec
    extends CatsEffectSuite
    with doodle.svg.algebra.TestAlgebraModule {
  import scalatags.Text.{svgAttrs, svgTags}
  import scalatags.Text.implicits.*

  val blackStroke =
    Stroke(StrokeStyle.color(Color.black), 1.0, Cap.butt, Join.miter, None)

  test("circle renders to svg circle") {
    val diameter = 10.0
    val circle = new doodle.algebra.Picture[Basic, Unit] {
      def apply(implicit algebra: Basic): algebra.Drawing[Unit] =
        algebra.strokeColor(algebra.circle(diameter), Color.black)
    }
    val expected =
      svgTags.g(
        svgTags.defs(),
        svgTags.circle(
          svgAttrs.transform := Svg.toSvgTransform(
            Transform.verticalReflection
          ),
          svgAttrs.style := Svg
            .toStyle(Some(blackStroke), None, mutable.Set.empty),
          svgAttrs.r := (diameter / 2.0)
        )
      )

    Svg
      .renderWithoutRootTag(algebraInstance, circle)
      .map { case (_, elt, _) => elt }
      .assertEquals(expected)
  }

  test("paths of path elements render correctly") {
    import doodle.core.PathElement.*
    val path1 = "M 0,0 M 5,5 L 10,10 C 20,20 30,30 40,40 "
    val path2 = "M 0,0 M 5,5 L 10,10 C 20,20 30,30 40,40 Z"

    assertEquals(
      Svg
        .toSvgPath(
          List(moveTo(5, 5), lineTo(10, 10), curveTo(20, 20, 30, 30, 40, 40)),
          Svg.Open
        ),
      path1
    )

    assertEquals(
      Svg
        .toSvgPath(
          List(moveTo(5, 5), lineTo(10, 10), curveTo(20, 20, 30, 30, 40, 40)),
          Svg.Closed
        ),
      path2
    )
  }

  test("monospaced fonts render correctly") {
    import doodle.core.font.Font
    import doodle.core.font.FontFamily

    assertEquals(
      Svg
        .textTag(
          "abc",
          Font.defaultSansSerif.withFamily(FontFamily.monospaced),
          "stroke: none;"
        )
        .toString,
      """<text style="font-family: monospace; font-style: normal; font-weight: normal; font-size: 12pt; stroke: none;">abc</text>"""
    )

  }

  test("svgTag style without isolation matches original output") {
    val bb = BoundingBox.centered(100, 100)
    val frame = Frame("test").withSizedToPicture()
    val tag = Svg.svgTag(bb, frame)
    val rendered = tag.toString

    assert(
      rendered.contains("""style="pointer-events: bounding-box;""""),
      s"Expected no trailing space or extra properties in style, got: $rendered"
    )
    assert(
      !rendered.contains("isolation:"),
      s"Expected no isolation in style, got: $rendered"
    )
  }

  test("svgTag style with isolation emits isolation property") {
    val bb = BoundingBox.centered(100, 100)
    val frame =
      Frame("test").withSizedToPicture().withIsolation(Isolation.Isolate)
    val tag = Svg.svgTag(bb, frame)
    val rendered = tag.toString

    assert(
      rendered.contains("isolation: isolate;"),
      s"Expected isolation: isolate; in style, got: $rendered"
    )
  }

  test("svgTag style with background and isolation emits both") {
    val bb = BoundingBox.centered(100, 100)
    val frame = Frame("test")
      .withSizedToPicture()
      .withBackground(Color.white)
      .withIsolation(Isolation.Isolate)
    val tag = Svg.svgTag(bb, frame)
    val rendered = tag.toString

    assert(
      rendered.contains("background-color:"),
      s"Expected background-color in style, got: $rendered"
    )
    assert(
      rendered.contains("isolation: isolate;"),
      s"Expected isolation: isolate; in style, got: $rendered"
    )
  }

  test("svgTag style with Isolation.Auto emits auto") {
    val bb = BoundingBox.centered(100, 100)
    val frame = Frame("test").withSizedToPicture().withIsolation(Isolation.Auto)
    val tag = Svg.svgTag(bb, frame)
    val rendered = tag.toString

    assert(
      rendered.contains("isolation: auto;"),
      s"Expected isolation: auto; in style, got: $rendered"
    )
  }

  test("svgTag FixedSize style without isolation has no trailing space") {
    val bb = BoundingBox.centered(100, 100)
    val frame = Frame("test").withSize(200, 200)
    val tag = Svg.svgTag(bb, frame)
    val rendered = tag.toString

    assert(
      rendered.contains("""style="pointer-events: bounding-box;""""),
      s"Expected clean style without trailing space, got: $rendered"
    )
    assert(
      !rendered.contains("isolation:"),
      s"Expected no isolation in style, got: $rendered"
    )
  }

  test("svgTag FixedSize style with isolation emits isolation property") {
    val bb = BoundingBox.centered(100, 100)
    val frame =
      Frame("test").withSize(200, 200).withIsolation(Isolation.Isolate)
    val tag = Svg.svgTag(bb, frame)
    val rendered = tag.toString

    assert(
      rendered.contains("isolation: isolate;"),
      s"Expected isolation: isolate; in style, got: $rendered"
    )
  }
}
