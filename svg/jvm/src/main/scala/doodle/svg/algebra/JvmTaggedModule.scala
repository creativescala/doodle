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

package doodle.svg.algebra

import doodle.svg.JvmBase
import scalatags.generic.Attr

trait JvmTaggedModule extends JvmBase {
  trait JvmTagged extends Tagged[Tag] {
    self: doodle.algebra.Algebra {
      type Drawing[A] = doodle.algebra.generic.Finalized[SvgResult, A]
    } =>

    import bundle.implicits.stringAttr
    import bundle.tags

    /** Wrap the given Tag around the given Drawing. */
    def tagged[A](drawing: Drawing[A], tag: Tag): Drawing[A] =
      drawing.map((bb, rdr) =>
        val newRdr = rdr.map { case (svg, other, a) =>
          (
            tag.apply(svg.asInstanceOf[bundle.Modifier]).asInstanceOf[Tag],
            other,
            a
          )
        }
        (bb, newRdr)
      )

    /** A utility to include wrap a link (an a tag) around a Drawing. */
    def link[A](drawing: Drawing[A], href: String): Drawing[A] =
      tagged(drawing, tags.a(bundle.attrs.href := href))

    def attribute[A](
        drawing: Drawing[A],
        attrLabel: String,
        value: String
    ): Drawing[A] =
      tagged(drawing, tags.div(Attr(attrLabel) := value))

  }
}
