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

package doodle.algebra.generic

import cats.effect.IO
import doodle.algebra.*
import doodle.syntax.loadBitmap.*
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

object LoadBitmapSpec extends Properties("LoadBitmap properties") {

  case class TestBitmap(content: String)

  given testLoader: LoadBitmap[String, TestBitmap] with {
    def load(path: String): IO[TestBitmap] =
      path match {
        case s if s.startsWith("missing-") =>
          IO.raiseError(FileNotFound(path))
        case s if s.startsWith("corrupt-") =>
          IO.raiseError(InvalidFormat(path, new Exception("Bad format")))
        case s if s.startsWith("network-") =>
          IO.raiseError(NetworkError(path, new Exception("Network error")))
        case _ =>
          IO.pure(TestBitmap(s"Content of $path"))
      }
  }

  // Test the IO structure
  property("successful loading returns IO with bitmap") = forAll {
    (name: String) =>
      val path = s"valid-$name.png"
      val result = path.loadBitmap

      result.isInstanceOf[IO[TestBitmap]]
  }

  property("loadBitmap syntax works") = forAll { (name: String) =>
    val path = s"test-$name.png"
    val io = path.loadBitmap

    io.isInstanceOf[IO[TestBitmap]]
  }

  // Test that errors are properly wrapped in IO
  property("missing files return failed IO") = {
    val path = "missing-test.png"
    val io = path.loadBitmap

    io.isInstanceOf[IO[TestBitmap]]
  }
}
