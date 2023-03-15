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
package interact
package animation

import cats.implicits._
import doodle.interact.syntax.all._
import org.scalacheck.Prop._
import org.scalacheck._
import munit.ScalaCheckSuite

class InterpolationSpec extends ScalaCheckSuite {
  property("upTo empty range produces no output") {
    forAllNoShrink { (x: Double) =>
      assertEquals(x.upTo(x).forSteps(10).toList, List())
    }
  }

  property("upToIncluding empty range produces single output") {
    forAllNoShrink { (x: Double) =>
      assertEquals(x.upToIncluding(x).forSteps(10).toList, List(x))
    }
  }

  property("upTo is empty when steps is zero") {
    forAllNoShrink(
      Gen.choose(-100.0, 100.0) :| "Start",
      Gen.posNum[Double] :| "Difference"
    ) { (start, difference) =>
      val t = start.upTo(start + difference).forSteps(0)
      assertEquals(t.toList, List.empty)
    }
  }

  property("upToIncluding is empty when steps is zero") {
    forAllNoShrink(
      Gen.choose(-100.0, 100.0) :| "Start",
      Gen.posNum[Double] :| "Difference"
    ) { (start, difference) =>
      val t = start.upToIncluding(start + difference).forSteps(0)
      assertEquals(t.toList, List.empty)
    }
  }

  property("upTo produces requested number of steps when range is not empty") {
    forAllNoShrink(
      Gen.choose(-100.0, 100.0) :| "Start",
      Gen.posNum[Double] :| "Difference",
      Gen.choose(1L, 100L) :| "Steps"
    ) { (start, difference, steps) =>
      val t = start.upTo(start + difference).forSteps(steps)
      assertEquals(t.toList.length, steps.toInt)
    }
  }

  property(
    "upToIncluding produces requested number of steps when range is not empty"
  ) {
    forAllNoShrink(
      Gen.choose(-100.0, 100.0) :| "Start",
      Gen.posNum[Double] :| "Difference",
      Gen.choose(1L, 100L) :| "Steps"
    ) { (start, difference, steps) =>
      val t = start.upToIncluding(start + difference).forSteps(steps)
      assertEquals(t.toList.length, steps.toInt)
    }
  }

  property("upTo produces expected data") {
    forAllNoShrink(
      Gen.choose(0, 100) :| "Start",
      Gen.choose(0, 100) :| "Difference"
    ) { (start, difference) =>
      if (difference == 0) {
        val t = start.toDouble.upTo(start.toDouble + difference).forSteps(1)
        assertEquals(t.toList, List.empty[Double])
      } else {
        val t = start.toDouble
          .upTo(start.toDouble + difference)
          .forSteps(difference.toLong)
        assertEquals(
          t.toList,
          List.tabulate(difference) { a =>
            start.toDouble + a
          }
        )
      }
    }
  }

  property("upToIncluding produces expected data") {
    forAllNoShrink(
      Gen.choose(0, 100) :| "Start",
      Gen.choose(0, 100) :| "Difference"
    ) { (start, difference) =>
      val t = start.toDouble
        .upToIncluding(start.toDouble + difference)
        .forSteps(difference.toLong + 1)
      difference match {
        case 0 =>
          assertEquals(t.toList, List(start.toDouble))
        case _ =>
          assertEquals(
            t.toList,
            List.tabulate(difference + 1) { a =>
              start.toDouble + a
            }
          )
      }
    }
  }

  property("upToIncluding ends on stop") {
    forAllNoShrink(
      Gen.choose(-100.0, 100.0) :| "Start",
      Gen.posNum[Double] :| "Difference",
      Gen.choose(1L, 100L) :| "Steps"
    ) { (start, difference, steps) =>
      val t = start.upToIncluding(start + difference).forSteps(steps)
      assertEquals(t.toList.last, (start + difference))
    }
  }

  property("map transforms data") {
    forAllNoShrink(
      Gen.choose(-100.0, 100.0) :| "Start",
      Gen.posNum[Double] :| "Difference",
      Gen.choose(1L, 100L) :| "Steps"
    ) { (start, difference, steps) =>
      val t =
        start.upToIncluding(start + difference).map(a => -a).forSteps(steps)
      assertEquals(t.toList.last, -(start + difference))
    }
  }

  property("constant is always constant") {
    forAllNoShrink(
      Gen.choose(-100.0, 100.0) :| "Value",
      Gen.choose(1L, 100L) :| "Steps"
    ) { (value, steps) =>
      val t = Interpolation.constant(value).forSteps(steps)
      val l = t.toList
      assertEquals(l.size.toLong, steps)
      assert(l.forall(x => x == value))
    }
  }
}
