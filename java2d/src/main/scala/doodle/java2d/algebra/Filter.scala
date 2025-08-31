package doodle
package java2d
package algebra

import cats.Eval
import cats.data.WriterT
import doodle.algebra.{Filter as FilterAlgebra, Kernel}
import doodle.algebra.generic.*
import doodle.core.{BoundingBox, Color, Transform as Tx}
import doodle.java2d.algebra.reified.*
import doodle.java2d.effect.Java2d
import java.awt.image.{BufferedImage, ConvolveOp, Kernel as AwtKernel}
import java.awt.{Graphics2D, RenderingHints, AlphaComposite}

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
    // Note: Java2D drop shadow is complex and requires multiple operations
    // This is a simplified implementation - full implementation would need:
    // 1. Extract alpha channel as shadow
    // 2. Apply blur to shadow
    // 3. Offset shadow
    // 4. Colorize shadow
    // 5. Composite original over shadow
    // For now, we'll implement a basic version
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
        val width = Math.max(1, Math.ceil(bb.width).toInt)
        val height = Math.max(1, Math.ceil(bb.height).toInt)

        val image =
          new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g2d = image.createGraphics()
        setupGraphics(g2d)

        g2d.setComposite(AlphaComposite.Clear)
        g2d.fillRect(0, 0, width, height)
        g2d.setComposite(AlphaComposite.SrcOver)

        val tx = Tx.translate(width / 2.0, height / 2.0)

        // Render the current drawing to the BufferedImage
        val (txResult, renderable) = rdr.run(tx).value
        val (reification, a) = renderable.run.value

        Java2d.render(g2d, reification, txResult)
        g2d.dispose()

        val filtered = transform(image)

        // Create new Reification from the filtered image
        val newReification: Reification[A] = WriterT
          .tell[Eval, List[Reified]](
            List(Reified.bitmap(Tx.identity, filtered))
          )
          .map(_ => a)

        val newRenderable: Renderable[Reification, A] =
          Renderable.apply(_ => Eval.now(newReification))

        // Update bounding box if image size changed
        val newBB =
          if filtered.getWidth != width || filtered.getHeight != height then
            BoundingBox.centered(
              filtered.getWidth.toDouble,
              filtered.getHeight.toDouble
            )
          else bb

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

  private def applyGaussianBlur(
      image: BufferedImage,
      stdDev: Double
  ): BufferedImage = {
    val radius = Math.ceil(stdDev * 3).toInt
    val size = 2 * radius + 1
    val kernel = createGaussianKernel(size, stdDev)

    val awtKernel = new AwtKernel(size, size, kernel)
    val op = new ConvolveOp(awtKernel, ConvolveOp.EDGE_NO_OP, null)
    op.filter(image, null)
  }

  private def createGaussianKernel(size: Int, stdDev: Double): Array[Float] = {
    val kernel = Array.ofDim[Float](size * size)
    val mean = size / 2
    var sum = 0.0

    for {
      y <- 0 until size
      x <- 0 until size
    } {
      val value = Math.exp(
        -0.5 * (Math.pow((x - mean) / stdDev, 2.0) +
          Math.pow((y - mean) / stdDev, 2.0))
      ) / (2 * Math.PI * stdDev * stdDev)
      kernel(y * size + x) = value.toFloat
      sum += value
    }

    // Normalize
    kernel.map(v => (v / sum).toFloat)
  }

  private def applyConvolution(
      image: BufferedImage,
      kernel: Kernel,
      divisor: Option[Double],
      bias: Double
  ): BufferedImage = {
    val kernelArray =
      IArray.genericWrapArray(kernel.elements.map(_.toFloat)).toArray
    val actualDivisor = divisor.getOrElse(kernelArray.sum.toDouble)

    val normalizedKernel =
      if actualDivisor != 1.0 then
        kernelArray.map(v => (v / actualDivisor).toFloat)
      else kernelArray

    val awtKernel = new AwtKernel(kernel.width, kernel.height, normalizedKernel)
    val op = new ConvolveOp(awtKernel, ConvolveOp.EDGE_NO_OP, null)

    var result = op.filter(image, null)

    // Apply bias if needed
    if bias != 0.0 then {
      result = applyBias(result, bias)
    }

    result
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
    // Calculate required size including shadow
    val extraSpace = Math.ceil(blur * 3).toInt
    val newWidth = image.getWidth + Math.abs(offsetX) + 2 * extraSpace
    val newHeight = image.getHeight + Math.abs(offsetY) + 2 * extraSpace

    val result =
      new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB)
    val g2d = result.createGraphics()
    setupGraphics(g2d)

    // Extract alpha channel and create shadow
    val shadow = extractAlphaAsShadow(image, shadowColor)

    // Apply blur to shadow
    val blurredShadow =
      if blur > 0 then applyGaussianBlur(shadow, blur) else shadow

    // Draw shadow with offset
    val shadowX = if offsetX >= 0 then offsetX + extraSpace else extraSpace
    val shadowY = if offsetY >= 0 then offsetY + extraSpace else extraSpace
    g2d.drawImage(blurredShadow, shadowX, shadowY, null)

    // Draw original image on top
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
