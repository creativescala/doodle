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

import scala.reflect.ClassTag

/** A BlockingQueue with finite capacity where writes always succeed.
  *
  * Writes always succeed, overwriting existing values in a FIFO manner. Reads
  * may block until a value is available. This is appropriate for interact
  * applications, where getting the latest data is more important than getting
  * all the data.
  */
final class BlockingCircularQueue[A: ClassTag](capacity: Int) {
  private val data: Array[A] = Array.ofDim(capacity)
  private var writeIdx = 0
  private var readIdx = 0

  private val readLock = Object()
  private val writeLock = Object()

  // Number of elements that can be read from data. 0 <= readable < capacity
  var readable = 0

  def add(e: A): Boolean = {
    // Acquire the monitor, so we can safely modify internal state and notify
    // readers waiting on it when we've added the element.
    writeLock.synchronized {
      readLock.synchronized {

        try {
          data(writeIdx) = e
          readable = (readable + 1).min(capacity)

          // If we just caught up to readIdx, increement readIdx so it now points
          // to the oldest data
          if writeIdx == readIdx && readable == capacity
          then readIdx = (readIdx + 1) % capacity

          writeIdx = (writeIdx + 1) % capacity
        } finally {
          // Wake up any readers waiting for data
          readLock.notifyAll()
        }
      }

      true
    }
  }

  def take(): A = {
    readLock.synchronized {
      while readable == 0 do {
        readLock.wait()
      }

      val elt = data(readIdx)
      readIdx = (readIdx + 1) % capacity
      readable = readable - 1

      elt
    }
  }
}
