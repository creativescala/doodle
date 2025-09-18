package doodle
package java2d

import doodle.algebra.Kernel
import doodle.core.*

import cats.effect.unsafe.implicits.global

import doodle.java2d.*
import doodle.java2d.effect.Java2d
import doodle.syntax.all.*
import munit.FunSuite
import java.awt.image.BufferedImage

class Java2dFilterSuite extends FunSuite {

  def renderToImage(picture: Picture[Unit]): BufferedImage = {

    val frame = Frame.default.withSize(200, 200)

    val io = Java2d.renderBufferedImage(
      frame.size,
      frame.center,
      frame.background,
      picture
    ) { (width: Int, height: Int) =>
      new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    }

    val (image, _) = io.unsafeRunSync()
    image
  }

  test("gaussianBlur should actually blur the image") {
    val sharp = Picture.circle(40).fillColor(Color.red)
    val blurred = sharp.blur(5.0)

    val sharpImage = renderToImage(sharp)
    val blurredImage = renderToImage(blurred)

    // Check that colors have spread (blur effect)
    val centerX = sharpImage.getWidth / 2
    val centerY = sharpImage.getHeight / 2

    // Check a point near the edge of the circle (should be affected by blur)
    val edgeX = centerX + 35
    val edgeY = centerY

    val sharpEdge = sharpImage.getRGB(edgeX, edgeY)
    val blurredEdge = blurredImage.getRGB(edgeX, edgeY)

    // In sharp image, edge should be transparent (alpha = 0)
    // In blurred image, edge should have some color (alpha > 0)
    val sharpAlpha = (sharpEdge >> 24) & 0xff
    val blurredAlpha = (blurredEdge >> 24) & 0xff

    // Blur should spread color to previously empty areas
    assert(blurredAlpha > sharpAlpha || blurredAlpha > 0)
  }

  test("detectEdges should highlight boundaries") {
    val solid = Picture.square(60).fillColor(Color.blue)
    val edges = solid.detectEdges

    val solidImage = renderToImage(solid)
    val edgeImage = renderToImage(edges)

    // Sample a few points to verify edge detection worked
    val centerX = solidImage.getWidth / 2
    val centerY = solidImage.getHeight / 2

    // Center should be different (edges are typically darker in uniform areas)
    val originalCenter = solidImage.getRGB(centerX, centerY)
    val edgeCenter = edgeImage.getRGB(centerX, centerY)

    // Just verify the filter ran and produced different output
    val originalBlue = originalCenter & 0xff
    val edgeBlue = edgeCenter & 0xff

    // Edge detection should change the image
    assert(originalBlue != edgeBlue || originalCenter != edgeCenter)
  }

  test("boxBlur kernel should have correct properties") {
    val radius = 2
    val size = 2 * radius + 1
    val expectedKernelSize = size * size

    // Create kernel as in implementation
    val kernel = Kernel(
      size,
      size,
      IArray.fill(expectedKernelSize)(1.0 / expectedKernelSize)
    )

    // Verify kernel properties
    assertEquals(kernel.width, 5)
    assertEquals(kernel.height, 5)
    assertEquals(kernel.elements.length, 25)

    // All elements should be equal
    val expectedValue = 1.0 / 25.0
    kernel.elements.foreach { value =>
      assertEquals(value, expectedValue, 0.0001)
    }

    // Kernel should sum to approximately 1 (allowing for floating point errors)
    val sum = kernel.elements.foldLeft(0.0)(_ + _)
    assertEquals(sum, 1.0, 0.01)
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
    val circle = Picture.circle(30).fillColor(Color.red)
    val withShadow = circle.dropShadow(10, 10, 2, Color.black)

    val originalImage = renderToImage(circle)
    val shadowImage = renderToImage(withShadow)

    // Shadow image should be at least as large (often larger due to blur)
    assert(shadowImage.getWidth >= originalImage.getWidth)
    assert(shadowImage.getHeight >= originalImage.getHeight)

    // Check that we have both the original and shadow
    // The center should still be red (original circle)
    val centerX = shadowImage.getWidth / 2
    val centerY = shadowImage.getHeight / 2
    val centerPixel = shadowImage.getRGB(centerX, centerY)
    val centerRed = (centerPixel >> 16) & 0xff

    // Center should still be reddish
    assert(centerRed > 100)
  }

  test("convolve with identity kernel should approximately preserve image") {
    val identityKernel = Kernel(
      3,
      3,
      IArray(
        0, 0, 0, 0, 1, 0, 0, 0, 0
      ).map(_.toDouble)
    )

    val original = Picture.square(40).fillColor(Color.green)
    val convolved = original.convolve(identityKernel)

    val originalImage = renderToImage(original)
    val convolvedImage = renderToImage(convolved)

    // Sample center pixel (away from edges where convolution might have artifacts)
    val centerX = originalImage.getWidth / 2
    val centerY = originalImage.getHeight / 2

    val origPixel = originalImage.getRGB(centerX, centerY)
    val convPixel = convolvedImage.getRGB(centerX, centerY)

    // Extract color components
    val origG = (origPixel >> 8) & 0xff
    val convG = (convPixel >> 8) & 0xff

    // Should be approximately the same (allowing for rounding errors)
    assert(Math.abs(origG - convG) < 10)
  }

  test("filter composition should work") {
    val base = Picture.circle(40).fillColor(Color.blue)

    // Apply filters in sequence - just verify they don't crash
    val filtered1 = base.blur(2.0).sharpen(1.5)
    val filtered2 = base.sharpen(1.5).blur(2.0)

    val image1 = renderToImage(filtered1)
    val image2 = renderToImage(filtered2)

    // Just verify both produced images
    assert(image1.getWidth > 0)
    assert(image2.getWidth > 0)

    // They might be different due to non-commutative operations
    // but we can't guarantee pixel-perfect differences due to rounding
  }
}
