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

import java.util.concurrent.TimeUnit

object Animator extends doodle.engine.Animator[Java2DFrame] {
  def onFrame(canvas: Java2DFrame)(cb: => Unit): () => Unit = {
    val command =
      new Runnable {
        def run(): Unit = {
          // println("Runnable fired")
          cb
        }
      }


    val future = canvas.timer.scheduleWithFixedDelay(command, 0, 16, TimeUnit.MILLISECONDS)
    val cancel = () => { future.cancel(false); () }
    cancel
  }
}
