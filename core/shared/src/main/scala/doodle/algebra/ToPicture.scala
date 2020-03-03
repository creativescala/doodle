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
package algebra

/**
 * Algebra that represents converting from the Input type to a F[Unit] (that in
 * turn can be wrapped in a Picture). This can be used to represent, for
 * example, creating a picture from a bitmap in a base64 encoded string.
 */
trait ToPicture[F[_], Input] extends Algebra[F] {
  def toPicture(in: Input): F[Unit]
}
