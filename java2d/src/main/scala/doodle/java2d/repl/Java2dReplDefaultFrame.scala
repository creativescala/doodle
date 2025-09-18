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

package doodle.java2d.repl

import doodle.effect.DefaultFrame
import doodle.java2d.effect.BlockingBehavior
import doodle.java2d.effect.Frame

object Java2dReplDefaultFrame extends DefaultFrame[Frame] {
  val default: Frame = Frame.default
    .withSizedToPicture(20)
    .withBlockingBehavior(BlockingBehavior.DoNotBlock)
}
