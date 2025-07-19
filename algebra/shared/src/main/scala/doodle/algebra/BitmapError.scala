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
package algebra

/** Base type for all bitmap loading errors */
sealed abstract class BitmapError extends Exception {
  def message: String
  override def getMessage: String = message
}

/** The specified file was not found */
final case class FileNotFound(path: String) extends BitmapError {
  val message = s"File not found: $path"
}

/** The file exists but is not a valid image format */
final case class InvalidFormat(path: String, cause: Throwable)
    extends BitmapError {
  val message = s"Invalid image format: $path"
  override def getCause: Throwable = cause
}

/** Network error when loading from URL */
final case class NetworkError(url: String, cause: Throwable)
    extends BitmapError {
  val message = s"Network error loading: $url"
  override def getCause: Throwable = cause
}

/** Generic IO error */
final case class IOError(description: String, cause: Throwable)
    extends BitmapError {
  val message = s"IO error: $description"
  override def getCause: Throwable = cause
}
