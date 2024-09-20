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

package doodle.java2d.effect

import doodle.algebra.generic.Finalized
import doodle.core.Transform
import doodle.java2d.Picture
import doodle.java2d.algebra.Algebra
import doodle.java2d.algebra.reified.Reification
import doodle.java2d.algebra.reified.Reified

import java.util.concurrent.CompletableFuture

/** Event that is passed into Java2DPanel to request rendering of a Picture.
  * This crosses the boundary between the Cats Effect and Swing threading model.
  */
private[effect] final case class RenderRequest[A](
    picture: Picture[A],
    onComplete: CompletableFuture[A]
) {

  /** Convert the picture into a form that can drawn and as a side effect
    * complete onComplete with the RenderResult.
    */
  def render(
      frame: Frame,
      algebra: Algebra
  ): RenderResult[A] = {
    val drawing: Finalized[Reification, A] = picture(algebra)
    val (bb, rdr) = drawing.run(List.empty).value
    val (w, h) = Java2d.size(bb, frame.size)
    val (_, fa) = rdr.run(Transform.identity).value
    val (reified, a) = fa.run.value

    val result = RenderResult(reified, bb, w, h, a)
    onComplete.complete(a)
    result
  }
}
