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

import cats.Eval
import doodle.algebra.Filter
import doodle.algebra.Kernel
import doodle.algebra.generic.*
import doodle.core.Color

import scala.util.Random

trait FilterModule { root: Base with SvgModule =>
  trait Filter extends doodle.algebra.Filter {
    self: doodle.algebra.Algebra {
      type Drawing[A] = doodle.algebra.generic.Finalized[SvgResult, A]
    } =>

    def gaussianBlur[A](picture: Drawing[A], stdDeviation: Double): Drawing[A] =
      picture.flatMap { (bb, rdr) =>
        Finalized.leaf { dc =>
          val filterId = s"blur-${Random.nextLong().toHexString}"
          val filterTag = createGaussianBlurFilter(filterId, stdDeviation)

          val newRdr: Renderable[SvgResult, A] = rdr.map { result =>
            val (tag, defs, a) = result

            defs += filterTag

            val filteredTag = applyFilter(tag, filterId)

            (filteredTag, defs, a)
          }

          (bb, newRdr)
        }
      }

    def boxBlur[A](picture: Drawing[A], radius: Int): Drawing[A] = {
      val size = 2 * radius + 1
      val kernel =
        Kernel(size, size, IArray.fill(size * size)(1.0 / (size * size)))

      convolveMatrix(picture, kernel, Some(1.0), 0.0)
    }

    def detectEdges[A](picture: Drawing[A]): Drawing[A] =
      convolveMatrix(picture, Filter.edgeDetectionKernel, None, 0.0)

    def sharpen[A](picture: Drawing[A], amount: Double): Drawing[A] = {
      val baseKernel = Filter.sharpenKernel
      val scaledElements = IArray.tabulate(baseKernel.elements.length)(i =>
        baseKernel.elements(i) * amount
      )
      val kernel = baseKernel.copy(elements = scaledElements)

      convolveMatrix(picture, kernel, None, 0.0)
    }

    def emboss[A](picture: Drawing[A]): Drawing[A] =
      convolveMatrix(picture, Filter.embossKernel, None, 0.0)

    def convolveMatrix[A](
        picture: Drawing[A],
        kernel: Kernel,
        divisor: Option[Double],
        bias: Double
    ): Drawing[A] =
      picture.flatMap { (bb, rdr) =>
        Finalized.leaf { dc =>
          val filterId = s"convolve-${Random.nextLong().toHexString}"
          val filterTag = createConvolveFilter(filterId, kernel, divisor, bias)

          val newRdr: Renderable[SvgResult, A] = rdr.map { result =>
            val (tag, defs, a) = result

            defs += filterTag

            val filteredTag = applyFilter(tag, filterId)

            (filteredTag, defs, a)
          }

          (bb, newRdr)
        }
      }

    def dropShadow[A](
        picture: Drawing[A],
        offsetX: Double,
        offsetY: Double,
        blur: Double,
        color: Color
    ): Drawing[A] =
      picture.flatMap { (bb, rdr) =>
        Finalized.leaf { dc =>
          val filterId = s"shadow-${Random.nextLong().toHexString}"
          val filterTag =
            createDropShadowFilter(filterId, offsetX, offsetY, blur, color)

          val newRdr: Renderable[SvgResult, A] = rdr.map { result =>
            val (tag, defs, a) = result

            defs += filterTag

            val filteredTag = applyFilter(tag, filterId)

            (filteredTag, defs, a)
          }

          (bb, newRdr)
        }
      }

    // Helper methods for creating SVG filters
    private def formatSvgNumber(value: Double): String = {
      if value == value.toInt.toDouble then {
        value.toInt.toString
      } else {
        value.toString
      }
    }

    private def createGaussianBlurFilter(id: String, stdDev: Double): Tag = {
      val b = bundle
      import b.implicits.*
      import b.{svgAttrs, svgTags}

      svgTags.tag("filter")(
        svgAttrs.id := id,
        svgTags.tag("feGaussianBlur")(
          svgAttrs.attr("in") := "SourceGraphic",
          svgAttrs.attr("stdDeviation") := formatSvgNumber(stdDev)
        )
      )
    }

    private def createConvolveFilter(
        id: String,
        kernel: Kernel,
        divisor: Option[Double],
        bias: Double
    ): Tag = {
      val b = bundle
      import b.implicits.*
      import b.{svgAttrs, svgTags}

      val kernelMatrix = kernel.elements.map(formatSvgNumber).mkString(" ")
      val div = divisor.getOrElse(kernel.sum)

      // Use single value for square kernels, two values for rectangular
      val orderValue = if kernel.width == kernel.height then {
        s"${kernel.width}"
      } else {
        s"${kernel.width} ${kernel.height}"
      }

      svgTags.tag("filter")(
        svgAttrs.id := id,
        svgTags.tag("feConvolveMatrix")(
          svgAttrs.attr("in") := "SourceGraphic",
          svgAttrs.attr("order") := orderValue,
          svgAttrs.attr("kernelMatrix") := kernelMatrix,
          svgAttrs.attr("divisor") := formatSvgNumber(div),
          svgAttrs.attr("bias") := formatSvgNumber(bias),
          svgAttrs.attr("edgeMode") := "duplicate"
        )
      )
    }

    private def createDropShadowFilter(
        id: String,
        dx: Double,
        dy: Double,
        stdDev: Double,
        color: Color
    ): Tag = {
      val b = bundle
      import b.implicits.*
      import b.{svgAttrs, svgTags}

      svgTags.tag("filter")(
        svgAttrs.id := id,
        svgTags.tag("feGaussianBlur")(
          svgAttrs.attr("in") := "SourceAlpha",
          svgAttrs.attr("stdDeviation") := formatSvgNumber(stdDev)
        ),
        svgTags.tag("feOffset")(
          svgAttrs.attr("dx") := formatSvgNumber(dx),
          svgAttrs.attr("dy") := formatSvgNumber(dy),
          svgAttrs.attr("result") := "offsetblur"
        ),
        svgTags.tag("feFlood")(
          svgAttrs.attr("flood-color") := Svg.toOklch(color),
          svgAttrs.attr("flood-opacity") := s"${color.alpha.get}"
        ),
        svgTags.tag("feComposite")(
          svgAttrs.attr("in2") := "offsetblur",
          svgAttrs.attr("operator") := "in"
        ),
        svgTags.tag("feMerge")(
          svgTags.tag("feMergeNode")(),
          svgTags.tag("feMergeNode")(
            svgAttrs.attr("in") := "SourceGraphic"
          )
        )
      )
    }

    private def applyFilter(tag: Tag, filterId: String): Tag = {
      val b = bundle
      import b.implicits.*
      import b.{svgAttrs, svgTags}

      // Wrap the tag in a group with the filter applied
      svgTags.g(
        svgAttrs.attr("filter") := s"url(#$filterId)",
        tag
      )
    }
  }
}
