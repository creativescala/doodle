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

import cats.Eval
import cats.data.WriterT
import doodle.algebra.Filter as FilterAlgebra
import doodle.algebra.Kernel
import doodle.algebra.generic.*
import doodle.core.BoundingBox
import doodle.core.Color
import doodle.core.Transform as Tx
import doodle.java2d.algebra.reified.*
import doodle.java2d.effect.Java2d

import java.awt.AlphaComposite
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.awt.image.ConvolveOp
import java.awt.image.ImagingOpException
import java.awt.image.Kernel as AwtKernel

/** Java2D implementation of the Filter algebra using BufferedImage operations
  */
trait FilterModule extends FilterAlgebra {
  self: Algebra =>

  def gaussianBlur[A](picture: Drawing[A], stdDeviation: Double): Drawing[A] =
    transformToBufferedImage(picture)(applyGaussianBlur(_, stdDeviation))

  def boxBlur[A](picture: Drawing[A], radius: Int): Drawing[A] = {
    val size = 2 * radius + 1
    val kernel =
      Kernel(size, size, IArray.fill(size * size)(1.0 / (size * size)))

    convolveMatrix(picture, kernel, Some(1.0), 0.0)
  }

  def detectEdges[A](picture: Drawing[A]): Drawing[A] =
    convolveMatrix(picture, FilterAlgebra.edgeDetectionKernel, Some(1.0), 0.5)

  def sharpen[A](picture: Drawing[A], amount: Double): Drawing[A] = {
    val baseKernel = FilterAlgebra.sharpenKernel
    val scaledElements = IArray.tabulate(baseKernel.elements.length)(i =>
      baseKernel.elements(i) * amount
    )
    val kernel = baseKernel.copy(elements = scaledElements)

    convolveMatrix(picture, kernel, None, 0.0)
  }

  def emboss[A](picture: Drawing[A]): Drawing[A] =
    convolveMatrix(picture, FilterAlgebra.embossKernel, None, 0.0)

  def dropShadow[A](
      picture: Drawing[A],
      offsetX: Double,
      offsetY: Double,
      blur: Double,
      color: Color
  ): Drawing[A] = {
    // NOTE: Java2d drop shadow is complex and requires multiple operations
    // This is a simplified implementation - full implementation would need:
    // 1. Extract alpha channel as shadow
    // 2. Apply blur to shadow
    // 3. Offset shadow
    // 4. Colorize shadow
    // 5. Composite original over shadow
    // For now, we'll implement a basic version
    // @todo Implement full drop shadow effect
    transformToBufferedImage(picture) { image =>
      applyDropShadow(image, offsetX.toInt, offsetY.toInt, blur, color)
    }
  }

  def convolveMatrix[A](
      picture: Drawing[A],
      kernel: Kernel,
      divisor: Option[Double],
      bias: Double
  ): Drawing[A] =
    transformToBufferedImage(picture) { image =>
      applyConvolution(image, kernel, divisor, bias)
    }

  private def transformToBufferedImage[A](
      picture: Drawing[A]
  )(transform: BufferedImage => BufferedImage): Drawing[A] =
    picture.flatMap { case (bb, rdr) =>
      Finalized.leaf { dc =>
        val padding = 40
        val width = Math.max(100, Math.ceil(bb.width).toInt + padding * 2)
        val height = Math.max(100, Math.ceil(bb.height).toInt + padding * 2)

        val sourceImage =
          new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g2d = sourceImage.createGraphics()
        setupGraphics(g2d)

        g2d.setComposite(AlphaComposite.Clear)
        g2d.fillRect(0, 0, width, height)
        g2d.setComposite(AlphaComposite.SrcOver)

        // Center the rendering in the padded space
        val centerX = width / 2.0
        val centerY = height / 2.0
        val tx = Tx.translate(centerX, centerY)

        val (txResult, renderable) = rdr.run(tx).value
        val (reification, a) = renderable.run.value

        Java2d.render(g2d, reification, txResult)
        g2d.dispose()

        val filteredImage = transform(sourceImage)

        // Calculate the offset to center the bitmap in the coordinate system
        val offsetX = -filteredImage.getWidth / 2.0
        val offsetY = -filteredImage.getHeight / 2.0
        val bitmapTransform = Tx.translate(offsetX, offsetY)

        val newReification: Reification[A] = WriterT
          .tell[Eval, List[Reified]](
            List(Reified.bitmap(bitmapTransform, filteredImage))
          )
          .map(_ => a)

        val newRenderable: Renderable[Reification, A] =
          Renderable.apply(_ => Eval.now(newReification))

        // Keep the original bounding box since we want the same logical size
        // but expand it slightly to account for filter effects
        val expansionFactor = 1.2 // 20% larger to account for blur/shadow effects
        val newBB = BoundingBox.centered(
          bb.width * expansionFactor,
          bb.height * expansionFactor
        )

        (newBB, newRenderable)
      }
    }

  private def setupGraphics(g2d: Graphics2D): Unit = {
    g2d.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON
    )
    g2d.setRenderingHint(
      RenderingHints.KEY_RENDERING,
      RenderingHints.VALUE_RENDER_QUALITY
    )
  }

  private def createGaussianKernel(size: Int, stdDev: Double): Array[Float] = {
    val kernel = Array.ofDim[Float](size * size)
    val center = size / 2
    var sum = 0.0f

    for {
      y <- 0 until size
      x <- 0 until size
    } {
      val dx = x - center
      val dy = y - center
      val distance = dx * dx + dy * dy
      val value = Math.exp(-distance / (2.0 * stdDev * stdDev)).toFloat
      kernel(y * size + x) = value
      sum += value
    }

    // Normalize kernel so sum equals 1
    for (i <- kernel.indices) {
      kernel(i) /= sum
    }

    kernel
  }

  private def applyGaussianBlur(
      image: BufferedImage,
      stdDev: Double
  ): BufferedImage = {
    if stdDev < 0.5 then return image

    val radius = Math.min(
      10,
      Math.ceil(stdDev * 3).toInt
    ) // Cap radius to prevent huge kernels
    val size = 2 * radius + 1
    val kernel = createGaussianKernel(size, stdDev)

    val awtKernel = new AwtKernel(size, size, kernel)

    val src = ensureCompatibleImage(image)
    val dest = new BufferedImage(
      src.getWidth,
      src.getHeight,
      BufferedImage.TYPE_INT_ARGB
    )

    val op = new ConvolveOp(awtKernel, ConvolveOp.EDGE_NO_OP, null)

    try {
      op.filter(src, dest)
      dest
    } catch {
      case _: ImagingOpException =>
        src
    }
  }

  private def applyConvolution(
      image: BufferedImage,
      kernel: Kernel,
      divisor: Option[Double],
      bias: Double
  ): BufferedImage = {
    val compatibleImage = ensureCompatibleImage(image)

    val kernelArray =
      Array.tabulate(kernel.elements.length)(i => kernel.elements(i).toFloat)
    val actualDivisor = divisor.getOrElse(kernelArray.sum.toDouble).toFloat

    val normalizedKernel =
      if Math.abs(actualDivisor - 1.0f) > 0.001f then
        kernelArray.map(v => v / actualDivisor)
      else kernelArray

    val awtKernel = new AwtKernel(kernel.width, kernel.height, normalizedKernel)

    val dest = new BufferedImage(
      compatibleImage.getWidth,
      compatibleImage.getHeight,
      BufferedImage.TYPE_INT_ARGB
    )

    val op = new ConvolveOp(awtKernel, ConvolveOp.EDGE_NO_OP, null)

    try {
      op.filter(compatibleImage, dest)
      if Math.abs(bias) > 0.001 then {
        applyBias(dest, bias)
      } else {
        dest
      }
    } catch {
      case _: ImagingOpException =>
        compatibleImage
    }
  }

  private def ensureCompatibleImage(image: BufferedImage): BufferedImage = {
    if image.getType == BufferedImage.TYPE_INT_ARGB then image
    else {
      val newImage = new BufferedImage(
        image.getWidth,
        image.getHeight,
        BufferedImage.TYPE_INT_ARGB
      )
      val g2d = newImage.createGraphics()
      g2d.drawImage(image, 0, 0, null)
      g2d.dispose()
      newImage
    }
  }

  private def applyBias(image: BufferedImage, bias: Double): BufferedImage = {
    val biasInt = (bias * 128).toInt

    for {
      y <- 0 until image.getHeight
      x <- 0 until image.getWidth
    } {
      val pixel = image.getRGB(x, y)
      val a = (pixel >> 24) & 0xff
      val r = Math.min(255, Math.max(0, ((pixel >> 16) & 0xff) + biasInt))
      val g = Math.min(255, Math.max(0, ((pixel >> 8) & 0xff) + biasInt))
      val b = Math.min(255, Math.max(0, (pixel & 0xff) + biasInt))

      image.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b)
    }

    image
  }

  private def applyDropShadow(
      image: BufferedImage,
      offsetX: Int,
      offsetY: Int,
      blur: Double,
      shadowColor: Color
  ): BufferedImage = {
    val extraSpace = Math.ceil(blur * 3).toInt
    val newWidth = image.getWidth + Math.abs(offsetX) + 2 * extraSpace
    val newHeight = image.getHeight + Math.abs(offsetY) + 2 * extraSpace

    val result =
      new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB)
    val g2d = result.createGraphics()
    setupGraphics(g2d)

    // Extract alpha channel and create shadow
    val shadow = extractAlphaAsShadow(image, shadowColor)

    val blurredShadow =
      if blur > 0 then applyGaussianBlur(shadow, blur) else shadow

    val shadowX = if offsetX >= 0 then offsetX + extraSpace else extraSpace
    val shadowY = if offsetY >= 0 then offsetY + extraSpace else extraSpace
    g2d.drawImage(blurredShadow, shadowX, shadowY, null)

    val origX =
      if offsetX >= 0 then extraSpace else Math.abs(offsetX) + extraSpace
    val origY =
      if offsetY >= 0 then extraSpace else Math.abs(offsetY) + extraSpace
    g2d.drawImage(image, origX, origY, null)

    g2d.dispose()
    result
  }

  private def extractAlphaAsShadow(
      image: BufferedImage,
      shadowColor: Color
  ): BufferedImage = {
    val result = new BufferedImage(
      image.getWidth,
      image.getHeight,
      BufferedImage.TYPE_INT_ARGB
    )

    val rgb = shadowColor.toRgb
    val sr = (rgb.r.get * 255).toInt
    val sg = (rgb.g.get * 255).toInt
    val sb = (rgb.b.get * 255).toInt
    val sa = (rgb.a.get * 255).toInt

    for {
      y <- 0 until image.getHeight
      x <- 0 until image.getWidth
    } {
      val pixel = image.getRGB(x, y)
      val alpha = (pixel >> 24) & 0xff

      // Create shadow pixel with original alpha modulated by shadow alpha
      val shadowAlpha = (alpha * sa) / 255
      val shadowPixel = (shadowAlpha << 24) | (sr << 16) | (sg << 8) | sb
      result.setRGB(x, y, shadowPixel)
    }

    result
  }
}
