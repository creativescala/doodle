/*
 * Copyright 2015 Noel Welsh
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

package doodle.core.format

/** Marker trait for types that represent image formats, such as PNG and JPEG.
  */
trait Format

// Each format has both a type and value level representation

/* Standard format type for PDF writer */
sealed trait Pdf extends Format
object Pdf extends Pdf
/* Standard format type for GIF writer */
sealed trait Gif extends Format
object Gif extends Pdf
/* Standard format type for PNG writer */
sealed trait Png extends Format
object Png extends Png
/* Standard format type for SVG writer */
sealed trait Svg extends Format
object Svg extends Svg
/* Standard format type for JPEG writer */
sealed trait Jpg extends Format
object Jpg extends Jpg
