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
package java2d
package engine

import cats.effect.IO
import doodle.engine.Frame
import javax.swing.JFrame

object Engine extends doodle.engine.Engine[Algebra, Drawing, Java2DFrame] {
  private var jFrames: List[JFrame] = List.empty

  def frame(description: Frame): IO[Java2DFrame] =
    IO{
      val jFrame = new Java2DFrame(description)
      jFrames.synchronized{ jFrames = jFrame :: jFrames }
      jFrame
    }

  def render[A](canvas: Java2DFrame)(f: Algebra => Drawing[A]): IO[A] =
    canvas.render(f)

  def stop(): Unit = {
    jFrames.synchronized{
      jFrames.foreach(_.dispose)
      jFrames = List.empty
    }
  }
}
