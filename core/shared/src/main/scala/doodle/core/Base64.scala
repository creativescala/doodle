/*
 * Copyright 2015-2020 Noel Welsh
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
package core

/**
  * Wrapper class for storing base-64 encoded bitmap data along with the format of that bitmap.
  *
  * E.g. val pngData = Base64[Png]("data here ...") represents a base-64 bitmap in Png format.
  *
  * By convention that formats in [[doodle.effect.Writer$]] should be used.
  */
final case class Base64[Format](value: String)
