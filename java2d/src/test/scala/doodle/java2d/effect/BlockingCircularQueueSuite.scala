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

import munit.FunSuite

class BlockingCircularQueueSuite extends FunSuite {
  test("Sequential access returns expected values") {
    val q = BlockingCircularQueue[Int](4)
    q.add(1)
    q.add(2)
    q.add(3)
    q.add(4)

    assertEquals(1, q.take())
    assertEquals(2, q.take())
    assertEquals(3, q.take())
    assertEquals(4, q.take())

    q.add(5)
    q.add(6)

    assertEquals(5, q.take())
    assertEquals(6, q.take())

    q.add(7)
    q.add(8)
    q.add(9)
    q.add(10)
    q.add(11)

    assertEquals(8, q.take())
    assertEquals(9, q.take())
    assertEquals(10, q.take())
    assertEquals(11, q.take())
  }
}
