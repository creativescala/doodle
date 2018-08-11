/*
 * Copyright 2015 noelwelsh
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
package engine

/**
  * The `Animator` typeclass describes a data type that can create an area to
  * render an image (a Canvas) and render an image to that Canvas.
  */
trait Animator[Canvas]{
  /** Register a callback that is called the whenever a frame should be rendered.
    * The result is a function that can cancel this callback. */
  def onFrame(canvas: Canvas)(cb: => Unit): () => Unit
}
