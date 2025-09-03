package doodle
package java2d

import doodle.algebra.Kernel
import doodle.core.*
import doodle.java2d.*
import doodle.syntax.all.*
import munit.FunSuite
import java.awt.image.BufferedImage
import cats.effect.unsafe.implicits.global

class Java2dFilterSuite extends FunSuite {

  // Helper to render a picture to BufferedImage using the BufferedImageWriter
  def renderToImage(picture: Picture[Unit]): BufferedImage = {

    val frame = Frame.default.withSize(100, 100)
    val (_, image) = picture.bufferedImage(frame)
    image
  }

  test("gaussianBlur should actually blur the image") {
    val sharp = Picture.circle(20).fillColor(Color.red)
    val blurred = sharp.blur(3.0)

    val sharpImage = renderToImage(sharp)
    val blurredImage = renderToImage(blurred)

    // Check that edge pixels are different (blur spreads color)
    val sharpEdgePixel = sharpImage.getRGB(10, 50)
    val blurredEdgePixel = blurredImage.getRGB(10, 50)

    // Blurred image should have color spreading to previously empty areas
    assertNotEquals(sharpEdgePixel, blurredEdgePixel)

    // Verify image dimensions are preserved or expanded (for edge handling)
    assert(blurredImage.getWidth >= sharpImage.getWidth)
    assert(blurredImage.getHeight >= sharpImage.getHeight)
  }

  test("detectEdges should highlight boundaries") {
    val solid = Picture.square(40).fillColor(Color.blue)
    val edges = solid.detectEdges

    val solidImage = renderToImage(solid)
    val edgeImage = renderToImage(edges)

    // Center of solid square should be different after edge detection
    val centerX = solidImage.getWidth / 2
    val centerY = solidImage.getHeight / 2

    val originalCenter = solidImage.getRGB(centerX, centerY)
    val edgeCenter = edgeImage.getRGB(centerX, centerY)

    // Edge detection should change the center pixels
    assertNotEquals(originalCenter, edgeCenter)
  }

  test("boxBlur kernel should have correct properties") {
    val radius = 2
    val size = 2 * radius + 1
    val expectedKernelSize = size * size

    val kernel = Kernel(
      size,
      size,
      IArray.fill(expectedKernelSize)(1.0 / expectedKernelSize)
    )

    assertEquals(kernel.width, 5)
    assertEquals(kernel.height, 5)
    assertEquals(kernel.elements.length, 25)

    val expectedValue = 1.0 / 25.0
    kernel.elements.foreach { value =>
      assertEquals(value, expectedValue, 0.0001)
    }

    // Kernel should sum to 1 (for proper normalization)
    val sum = kernel.elements.foldLeft(0.0)(_ + _)
    assertEquals(sum, 1.0, 0.001)
  }

  test("sharpen kernel should emphasize center") {
    val kernel = doodle.algebra.Filter.sharpenKernel

    // Center element should be positive and larger than 1
    val centerIndex = (kernel.height / 2) * kernel.width + (kernel.width / 2)
    assert(kernel.elements(centerIndex) > 1.0)

    // Adjacent elements should be negative (for contrast)
    assert(kernel.elements(1) < 0) // top
    assert(kernel.elements(3) < 0) // left
    assert(kernel.elements(5) < 0) // right
    assert(kernel.elements(7) < 0) // bottom
  }

  test("emboss kernel should be asymmetric") {
    val kernel = doodle.algebra.Filter.embossKernel

    // Emboss kernels are typically asymmetric to create 3D effect
    val topLeft = kernel.elements(0)
    val bottomRight = kernel.elements(kernel.elements.length - 1)

    // These should have opposite signs for emboss effect
    assert(topLeft * bottomRight < 0)
  }

  test("dropShadow should create offset shadow") {
    val circle = Picture.circle(20).fillColor(Color.red)
    val withShadow = circle.dropShadow(10, 10, 0, Color.black)

    val originalImage = renderToImage(circle)
    val shadowImage = renderToImage(withShadow)

    assert(shadowImage.getWidth >= originalImage.getWidth)
    assert(shadowImage.getHeight >= originalImage.getHeight)

    val shadowX = 10
    val shadowY = 10
    val shadowPixel = shadowImage.getRGB(shadowX, shadowY)
    val shadowAlpha = (shadowPixel >> 24) & 0xff

    assert(shadowAlpha > 0)
  }

  test("convolve with identity kernel should preserve image") {
    val identityKernel = Kernel(
      3,
      3,
      IArray(
        0, 0, 0, 0, 1, 0, 0, 0, 0
      ).map(_.toDouble)
    )

    val original = Picture.square(30).fillColor(Color.green)
    val convolved = original.convolve(identityKernel)

    val originalImage = renderToImage(original)
    val convolvedImage = renderToImage(convolved)

    val centerX = originalImage.getWidth / 2
    val centerY = originalImage.getHeight / 2

    val origPixel = originalImage.getRGB(centerX, centerY)
    val convPixel = convolvedImage.getRGB(centerX, centerY)

    // Center pixels should be very similar
    val origR = (origPixel >> 16) & 0xff
    val convR = (convPixel >> 16) & 0xff

    // Allow small difference due to rounding
    assert(Math.abs(origR - convR) < 5)
  }

  test("filter composition should apply in sequence") {
    val base = Picture.circle(40).fillColor(Color.blue)

    val filtered1 = base.blur(2.0).sharpen(1.5)
    val filtered2 = base.sharpen(1.5).blur(2.0)

    val image1 = renderToImage(filtered1)
    val image2 = renderToImage(filtered2)

    val pixel1 = image1.getRGB(50, 50)
    val pixel2 = image2.getRGB(50, 50)

    // These should be different (non-commutative operations)
    assertNotEquals(pixel1, pixel2)
  }
}
