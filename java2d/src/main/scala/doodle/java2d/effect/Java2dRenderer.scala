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
package effect

import cats.effect.IO
import doodle.effect.DefaultRenderer
import javax.swing.JFrame

object Java2dRenderer extends DefaultRenderer[Algebra, Drawing, Frame, Canvas] {
  private var jFrames: List[JFrame] = List.empty

  val default: Frame = Frame.fitToPicture()

  def canvas(description: Frame): IO[Canvas] =
    IO {
      val jFrame = new Canvas(description)
      jFrames.synchronized { jFrames = jFrame :: jFrames }
      jFrame
    }

  def render[A](canvas: Canvas)(picture: Picture[A]): IO[A] =
    canvas.render(picture)

  def stop(): Unit = {
    jFrames.synchronized {
      jFrames.foreach(_.dispose)
      jFrames = List.empty
    }
  }
}
