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

package doodle
package image

package object syntax {
  object all extends ImageSyntax with TraverseImageSyntax
  object image extends ImageSyntax
  object traverse extends TraverseImageSyntax

  /** The core object defines syntax for doodle.core, which is a convenient way
    * to avoid pulling in Algebra syntax that may conflict with Image.
    */
  object core
      extends doodle.syntax.AngleSyntax
      with doodle.syntax.NormalizedSyntax
      with doodle.syntax.UnsignedByteSyntax
}
