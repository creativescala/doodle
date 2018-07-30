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
import doodle.engine._
import javax.swing.JFrame

final class Java2DFrame(frame: Frame) extends JFrame(frame.title) {
  val panel = new Java2DPanel(frame)

  getContentPane().add(panel)
  pack()
  repaint()
  setVisible(true)

  def render[A](f: Algebra => Drawing[A]): IO[A] =
    panel.render(f)
}
