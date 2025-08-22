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

import cats.effect.unsafe.implicits.global
import doodle.algebra.FileNotFound
import doodle.algebra.InvalidFormat
import doodle.java2d.{*, given}
import doodle.syntax.all.*
import munit.FunSuite

import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO

class Java2dLoadBitmapSuite extends FunSuite {

  // Helper to create a test image file
  def createTestImage(path: Path, width: Int = 100, height: Int = 100): Unit = {
    val image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val graphics = image.createGraphics()

    graphics.setColor(java.awt.Color.RED)
    graphics.fillRect(0, 0, width, height)
    graphics.dispose()

    val _ = ImageIO.write(image, "png", path.toFile)
  }

  test("LoadBitmap should successfully load a valid image file") {
    val tempFile = Files.createTempFile("test", ".png")

    try {
      createTestImage(tempFile)
      val result = tempFile.toFile.loadBitmap.unsafeRunSync()

      assertEquals(result.getWidth, 100)
      assertEquals(result.getHeight, 100)
    } finally {
      val _ = Files.deleteIfExists(tempFile)
    }
  }

  test("LoadBitmap should return FileNotFound for non-existent file") {
    val nonExistentFile = new File("this-file-does-not-exist.png")

    val result = nonExistentFile.loadBitmap.attempt.unsafeRunSync()

    assert(result.isLeft)
    result.left.foreach { error =>
      assert(error.isInstanceOf[FileNotFound])
      assertEquals(
        error.asInstanceOf[FileNotFound].path,
        nonExistentFile.getPath
      )
    }
  }

  test("LoadBitmap should return InvalidFormat for non-image file") {
    val tempFile = Files.createTempFile("test", ".txt")

    try {
      Files.write(tempFile, "This is not an image".getBytes)
      val result = tempFile.toFile.loadBitmap.attempt.unsafeRunSync()

      assert(result.isLeft)
      result.left.foreach { error =>
        assert(error.isInstanceOf[InvalidFormat])
      }
    } finally {
      val _ = Files.deleteIfExists(tempFile)
    }
  }

  test("LoadBitmap should work with Path") {
    val tempPath = Files.createTempFile("test", ".png")

    try {
      createTestImage(tempPath)
      val result = tempPath.loadBitmap.unsafeRunSync()

      assertEquals(result.getWidth, 100)
      assertEquals(result.getHeight, 100)
    } finally {
      val _ = Files.deleteIfExists(tempPath)
    }
  }

  test("BufferedImage should convert to Picture using existing ToPicture") {
    val image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB)

    val picture = image.toPicture

    assert(picture != null)
  }

  test("Complete flow: load image and convert to Picture") {
    val tempFile = Files.createTempFile("test", ".png")

    try {
      createTestImage(tempFile, 200, 150)

      val bitmap = tempFile.toFile.loadBitmap.unsafeRunSync()
      val picture = bitmap.toPicture

      assertEquals(bitmap.getWidth, 200)
      assertEquals(bitmap.getHeight, 150)
      assert(picture != null)

      val directPicture =
        tempFile.toFile
          .loadToPicture[BufferedImage, doodle.java2d.Algebra]
          .unsafeRunSync()
      assert(directPicture != null)
    } finally {
      val _ = Files.deleteIfExists(tempFile)
    }
  }
}
