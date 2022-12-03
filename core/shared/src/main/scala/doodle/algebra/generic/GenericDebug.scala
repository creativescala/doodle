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
package algebra
package generic

import doodle.core.Color

trait GenericDebug[G[_]] extends Debug {
  self: Shape with Layout with GivenApply[G] {
    type Drawing[A] = Finalized[G, A]
  } =>

  import cats.implicits._

  def debug[A](
      picture: Finalized[G, A],
      color: Color = Color.crimson
  ): Finalized[G, A] =
    Finalized { ctxTxs =>
      picture
        .runA(ctxTxs)
        .flatMap { case (bb, rdr) =>
          val xOffset = (bb.right - (bb.width / 2))
          val yOffset = (bb.top - (bb.height / 2))
          val bbOutline = at(rectangle(bb.width, bb.height), xOffset, yOffset)
          val bbOrigin = circle(5.0)
          val bbPicture = on(bbOrigin, bbOutline)

          bbPicture
            .runA(
              List(dc =>
                dc.strokeColor(color)
                  .strokeWidth(1.0)
                  .noFill
              )
            )
            .map { case (_, rdrDebug) =>
              val fullRdr = Renderable { tx =>
                (rdr.runA(tx), rdrDebug.runA(tx)).mapN { (ga, gdbg) =>
                  (ga, gdbg).mapN((a, _) => a)
                }
              }
              (ctxTxs, (bb, fullRdr))
            }
        }
        .value
    }
}
