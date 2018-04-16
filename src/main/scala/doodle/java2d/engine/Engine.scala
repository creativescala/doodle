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

object Engine {
  private var jFrames: List[JFrame] = List.empty

  def frame[A](frame: Frame)(f: Algebra => Drawing[A]): IO[A] = {
    def cbHandler(cb: Either[Throwable, A] => Unit): Unit = {
      val jFrame = new Java2DFrame(frame, f, cb)
      jFrames.synchronized{ jFrames = jFrame :: jFrames }
      ()
    }
    IO.async(cbHandler)
  }

  def stop(): Unit = {
    jFrames.synchronized{
      jFrames.foreach(_.dispose)
      jFrames = List.empty
    }
  }
}
