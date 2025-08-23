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

import cats.effect.IO
import doodle.core.*
import doodle.svg.algebra.SvgImageRef
import doodle.svg.algebra.SvgLoadBitmap
import munit.CatsEffectSuite

class SvgLoadBitmapSpec
    extends CatsEffectSuite
    with doodle.svg.algebra.TestAlgebraModule {

  test("LoadBitmap creates correct SvgImageRef from URL") {
    IO {
      val url = "https://example.com/image.png"
      val imageRef = SvgImageRef(url)

      assertEquals(imageRef.href, url)
      assertEquals(imageRef.width, None)
      assertEquals(imageRef.height, None)
    }
  }

  test("LoadBitmap creates SvgImageRef with dimensions") {
    val url = "https://example.com/image.jpg"
    val width = 200.0
    val height = 150.0

    for {
      imageRef <- SvgLoadBitmap.withDimensions(url, width, height)
    } yield {
      assertEquals(imageRef.href, url)
      assertEquals(imageRef.width, Some(width))
      assertEquals(imageRef.height, Some(height))
    }
  }

  test("LoadBitmap creates SvgImageRef with width only") {
    val url = "image.svg"
    val width = 300.0

    for {
      imageRef <- SvgLoadBitmap.withWidth(url, width)
    } yield {
      assertEquals(imageRef.href, url)
      assertEquals(imageRef.width, Some(width))
      assertEquals(imageRef.height, None)
    }
  }

  test("LoadBitmap creates SvgImageRef with height only") {
    val url =
      "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg=="
    val height = 250.0

    for {
      imageRef <- SvgLoadBitmap.withHeight(url, height)
    } yield {
      assertEquals(imageRef.href, url)
      assertEquals(imageRef.width, None)
      assertEquals(imageRef.height, Some(height))
    }
  }

  test("image method creates Drawing with correct bounding box") {
    IO {
      val imageRef = SvgImageRef("test.png", Some(100), Some(100))
      val drawing = algebraInstance.image(imageRef)
      val bb = drawing.boundingBox

      assertEquals(bb.width, 100.0)
      assertEquals(bb.height, 100.0)
    }
  }

  test("image element is properly centered") {
    IO {
      val imageRef = SvgImageRef("centered.jpg", Some(200), Some(100))
      val drawing = algebraInstance.image(imageRef)
      val bb = drawing.boundingBox

      // Image should be centered at origin
      assertEquals(bb.left, -100.0)
      assertEquals(bb.right, 100.0)
      assertEquals(bb.top, 50.0)
      assertEquals(bb.bottom, -50.0)
    }
  }

  test("default dimensions are used when not specified") {
    IO {
      val imageRef = SvgImageRef("default.gif")
      val drawing = algebraInstance.image(imageRef)
      val bb = drawing.boundingBox

      // Should use default dimensions
      assertEquals(bb.width, 100.0)
      assertEquals(bb.height, 100.0)
    }
  }

  test("image renders to SVG image element") {
    val imageRef = SvgImageRef("render.png", Some(100), Some(80))
    // val drawing = algebraInstance.image(imageRef)

    // Create a simple picture that uses the image
    val picture = new doodle.algebra.Picture[TestAlgebra, Unit] {
      def apply(implicit algebra: TestAlgebra): algebra.Drawing[Unit] =
        algebra.image(imageRef)
    }

    Svg
      .renderWithoutRootTag(algebraInstance, picture)
      .map { case (_, tag, _) =>
        val tagStr = tag.render

        assert(tagStr.contains("<image"))
        assert(tagStr.contains("xlink:href=\"render.png\""))
        assert(tagStr.contains("width=\"100"))
        assert(tagStr.contains("height=\"80"))
        assert(tagStr.contains("x=\"-50"))
        assert(tagStr.contains("y=\"-40"))
      }
  }

  test("image without dimensions renders correctly") {
    val imageRef = SvgImageRef("nodims.svg")

    val picture = new doodle.algebra.Picture[TestAlgebra, Unit] {
      def apply(implicit algebra: TestAlgebra): algebra.Drawing[Unit] =
        algebra.image(imageRef)
    }

    Svg
      .renderWithoutRootTag(algebraInstance, picture)
      .map { case (_, tag, _) =>
        val tagStr = tag.render

        assert(tagStr.contains("<image"))
        assert(tagStr.contains("xlink:href=\"nodims.svg\""))

        // Should not have width/height attributes when not specified
        assert(!tagStr.contains("width=\"") || tagStr.contains("width=\"100"))
        assert(!tagStr.contains("height=\"") || tagStr.contains("height=\"100"))
      }
  }

  test("image with only width renders correctly") {
    val imageRef = SvgImageRef("width-only.jpg", Some(150), None)

    val picture = new doodle.algebra.Picture[TestAlgebra, Unit] {
      def apply(implicit algebra: TestAlgebra): algebra.Drawing[Unit] =
        algebra.image(imageRef)
    }

    Svg
      .renderWithoutRootTag(algebraInstance, picture)
      .map { case (_, tag, _) =>
        val tagStr = tag.render

        assert(tagStr.contains("<image"))
        assert(tagStr.contains("xlink:href=\"width-only.jpg\""))
        assert(tagStr.contains("width=\"150"))
        assert(tagStr.contains("x=\"-75"))
        // Height should not be specified
        assert(!tagStr.contains("height=\"") || tagStr.contains("height=\"150"))
      }
  }
}
