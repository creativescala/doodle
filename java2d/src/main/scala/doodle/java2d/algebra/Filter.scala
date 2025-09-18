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
import java.awt.{ Graphics2D, RenderingHints }

trait FilterModule extends FilterAlgebra {
  self: Algebra =>

  def gaussianBlur[A](picture: Drawing[A], stdDeviation: Double): Drawing[A] =
    transformPicture(picture)(applyGaussianBlur(_, stdDeviation))

  def boxBlur[A](picture: Drawing[A], radius: Int): Drawing[A] = {
    val size = 2 * radius + 1
    val kernel =
      Kernel(size, size, IArray.fill(size * size)(1.0 / (size * size)))
    convolveMatrix(picture, kernel, None, 0.0)
  }

  def detectEdges[A](picture: Drawing[A]): Drawing[A] =
    convolveMatrix(picture, FilterAlgebra.edgeDetectionKernel, None, 0.5)

  def sharpen[A](picture: Drawing[A], amount: Double): Drawing[A] = {
    val baseKernel = FilterAlgebra.sharpenKernel
    val scaledElements = IArray.tabulate(baseKernel.elements.length)(i =>
      baseKernel.elements(i) * amount
    )
    val kernel = baseKernel.copy(elements = scaledElements)
    convolveMatrix(picture, kernel, None, 0.0)
  }

  def emboss[A](picture: Drawing[A]): Drawing[A] =
    convolveMatrix(picture, FilterAlgebra.embossKernel, None, 0.5)

  def dropShadow[A](
      picture: Drawing[A],
      offsetX: Double,
      offsetY: Double,
      blur: Double,
      color: Color
  ): Drawing[A] =
    transformPicture(picture)(
      applyDropShadow(_, offsetX.toInt, offsetY.toInt, blur, color)
    )

  def convolveMatrix[A](
      picture: Drawing[A],
      kernel: Kernel,
      divisor: Option[Double],
      bias: Double
  ): Drawing[A] =
    transformPicture(picture)(applyConvolution(_, kernel, divisor, bias))

  private def transformPicture[A](
      picture: Drawing[A]
  )(transform: BufferedImage => BufferedImage): Drawing[A] = {
    picture.flatMap { case (bb, rdr) =>
      Finalized.leaf { _ =>
        val width = Math.max(1, Math.ceil(bb.width).toInt + 100)
        val height = Math.max(1, Math.ceil(bb.height).toInt + 100)

        val image =
          new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g2d = image.createGraphics()
        setupGraphics(g2d)

        // Transform to center
        val centerTx = Tx.translate(width / 2.0, height / 2.0)

        // Get the reification and render it
        val (txResult, renderable) = rdr.run(centerTx).value
        val (reification, a) = renderable.run.value

        Java2d.render(g2d, reification, txResult)
        g2d.dispose()

        val filtered = transform(image)

        // Create new bounding box
        val filteredBB = BoundingBox.centered(
          filtered.getWidth.toDouble,
          filtered.getHeight.toDouble
        )

        val newRenderable: Renderable[Reification, A] = Renderable { tx =>
          Eval.now(
            WriterT
              .liftF[Eval, List[Reified], A](
                Eval.now(a)
              )
              .tell(List(Reified.bitmap(tx, filtered)))
          )
        }

        (filteredBB, newRenderable)
      }
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
    g2d.setRenderingHint(
      RenderingHints.KEY_INTERPOLATION,
      RenderingHints.VALUE_INTERPOLATION_BILINEAR
    )
  }

  private def applyGaussianBlur(
      image: BufferedImage,
      stdDev: Double
  ): BufferedImage = {
    if stdDev < 0.1 then return image

    val radius = Math.min(15, Math.max(1, Math.ceil(stdDev * 2).toInt))
    val size = 2 * radius + 1

    val kernel = Array.ofDim[Float](size * size)
    var sum = 0.0f
    val sigma2 = 2.0f * stdDev.toFloat * stdDev.toFloat

    for y <- 0 until size; x <- 0 until size do {
      val dx = x - radius
      val dy = y - radius
      val value = Math.exp(-(dx * dx + dy * dy) / sigma2).toFloat
      kernel(y * size + x) = value
      sum += value
    }

    for i <- kernel.indices do {
      kernel(i) /= sum
    }

    applyKernel(image, kernel, size, size)
  }

  private def applyConvolution(
      image: BufferedImage,
      kernel: Kernel,
      divisor: Option[Double],
      bias: Double
  ): BufferedImage = {
    val kernelArray =
      Array.tabulate(kernel.elements.length)(i => kernel.elements(i).toFloat)
    val sum = kernelArray.sum
    val actualDivisor = divisor
      .getOrElse(if Math.abs(sum) < 0.001 then 1.0 else sum.toDouble)
      .toFloat

    val normalized = kernelArray.map(_ / actualDivisor)

    val result = applyKernel(image, normalized, kernel.width, kernel.height)

    if Math.abs(bias) > 0.001 then {
      applyBias(result, bias)
    } else {
      result
    }
  }

  private def applyKernel(
      image: BufferedImage,
      kernel: Array[Float],
      kw: Int,
      kh: Int
  ): BufferedImage = {
    val src = ensureARGB(image)
    val dest = new BufferedImage(
      src.getWidth,
      src.getHeight,
      BufferedImage.TYPE_INT_ARGB
    )

    try {
      val awtKernel = new AwtKernel(kw, kh, kernel)
      val op = new ConvolveOp(awtKernel, ConvolveOp.EDGE_NO_OP, null)
      op.filter(src, dest)
    } catch {
      case _: Exception =>
        manualConvolve(src, kernel, kw, kh, dest)
    }

    dest
  }

  private def manualConvolve(
      src: BufferedImage,
      kernel: Array[Float],
      kw: Int,
      kh: Int,
      dest: BufferedImage
  ): Unit = {
    val width = src.getWidth
    val height = src.getHeight
    val kcx = kw / 2
    val kcy = kh / 2

    for y <- 0 until height; x <- 0 until width do {
      var r = 0f
      var g = 0f
      var b = 0f
      var a = 0f

      for ky <- 0 until kh; kx <- 0 until kw do {
        val sx = Math.min(width - 1, Math.max(0, x + kx - kcx))
        val sy = Math.min(height - 1, Math.max(0, y + ky - kcy))
        val pixel = src.getRGB(sx, sy)
        val weight = kernel(ky * kw + kx)

        a += ((pixel >> 24) & 0xff) * weight
        r += ((pixel >> 16) & 0xff) * weight
        g += ((pixel >> 8) & 0xff) * weight
        b += (pixel & 0xff) * weight
      }

      val newPixel =
        (Math.min(255, Math.max(0, a.toInt)) << 24) |
          (Math.min(255, Math.max(0, r.toInt)) << 16) |
          (Math.min(255, Math.max(0, g.toInt)) << 8) |
          Math.min(255, Math.max(0, b.toInt))

      dest.setRGB(x, y, newPixel)
    }
  }

  private def ensureARGB(image: BufferedImage): BufferedImage = {
    if image.getType == BufferedImage.TYPE_INT_ARGB then {
      image
    } else {
      val argb = new BufferedImage(
        image.getWidth,
        image.getHeight,
        BufferedImage.TYPE_INT_ARGB
      )
      val g = argb.createGraphics()
      g.drawImage(image, 0, 0, null)
      g.dispose()
      argb
    }
  }

  private def applyBias(image: BufferedImage, bias: Double): BufferedImage = {
    val biasInt = (bias * 127).toInt
    for y <- 0 until image.getHeight; x <- 0 until image.getWidth do {
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
    val padding = Math.ceil(blur * 3).toInt + 5
    val newWidth = image.getWidth + Math.abs(offsetX) + padding * 2
    val newHeight = image.getHeight + Math.abs(offsetY) + padding * 2

    val result =
      new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB)
    val g2d = result.createGraphics()
    setupGraphics(g2d)

    // Create shadow from alpha
    val shadow = new BufferedImage(
      image.getWidth,
      image.getHeight,
      BufferedImage.TYPE_INT_ARGB
    )
    val rgb = shadowColor.toRgb

    for y <- 0 until image.getHeight; x <- 0 until image.getWidth do {
      val pixel = image.getRGB(x, y)
      val alpha = ((pixel >> 24) & 0xff) * rgb.a.get
      val shadowPixel =
        (alpha.toInt << 24) |
          ((rgb.r.get * 255).toInt << 16) |
          ((rgb.g.get * 255).toInt << 8) |
          (rgb.b.get * 255).toInt
      shadow.setRGB(x, y, shadowPixel)
    }

    val blurredShadow =
      if blur > 0.5 then applyGaussianBlur(shadow, blur) else shadow

    g2d.drawImage(blurredShadow, offsetX + padding, offsetY + padding, null)
    g2d.drawImage(image, padding, padding, null)

    g2d.dispose()
    result
  }
}
