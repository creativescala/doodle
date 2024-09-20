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

import cats.implicits.*
import munit.ScalaCheckSuite
import org.scalacheck.*
import org.scalacheck.Prop.*

class TransducerSpec extends ScalaCheckSuite {
  property("empty produces no output") {
    Transducer.empty[Int].toList ?= List.empty[Int]
  }

  property("pure produces just one output") {
    forAllNoShrink { (x: Int) =>
      Transducer.pure(x).toList ?= List(x)
    }
  }

  property("toList generates all elements from the transducer") {
    forAllNoShrink { (elts: List[Int]) =>
      Transducer(elts*).toList ?= elts
    }
  }

  property("product produces shortest pairs") {
    forAllNoShrink { (xs: List[Int], ys: List[Int]) =>
      Transducer(xs*).product(Transducer(ys*)).toList ?= xs.zip(ys)
    }
  }

  property("++ combines transducers in series") {
    forAllNoShrink { (xs: List[Int], ys: List[Int]) =>
      val t = Transducer.fromList(xs) ++ (Transducer.fromList(ys))

      t.toList ?= (xs ++ ys)
    }
  }

  property("and combines transducers in parallel") {
    forAllNoShrink { (xs: List[Int], ys: List[Int]) =>
      val (shortest, longest) =
        if xs.length < ys.length then (xs, ys) else (ys, xs)

      val pad = List.fill(longest.length - shortest.length)(0)
      val a = Transducer.fromList(shortest ++ pad)
      val b = Transducer.fromList(longest)

      a.and(b).toList ?= (shortest ++ pad).zip(longest).map { case (a, b) =>
        a + b
      }
    }
  }

  property("and retains last value of first transducer to stop") {
    forAllNoShrink { (x: Int, xs: List[Int]) =>
      val a = Transducer.fromList(x :: xs)
      val b = Transducer.pure(0)

      a.and(b).toList ?= (x :: xs)
    }
  }

  property("andThen passes last output to the second transducer") {
    forAllNoShrink { (xs: List[Int]) =>
      val a = Transducer.fromList(xs)
      val t = a.andThen(o => Transducer(o + 1))

      xs.length match {
        case 0 => t.toList ?= List.empty
        case n =>
          val o = xs.last
          val l = t.toList
          (l.length ?= n + 1) && (l.last ?= o + 1)
      }
    }
  }

  property("repeat repeats the given number of times") {
    forAllNoShrink(Gen.listOf(Gen.choose(0, 10)), Gen.choose(0, 10)) {
      (xs: List[Int], repeat: Int) =>
        val t = Transducer.fromList(xs).repeat(repeat)
        t.toList ?= List.fill(repeat)(xs).flatten
    }
  }

  property("repeatForever seems to repeat forever") {
    forAllNoShrink(Gen.listOf(Gen.choose(0, 10)), Gen.choose(1, 100)) {
      (xs: List[Int], repeat: Int) =>
        val t = Transducer.fromList(xs).repeatForever
        var count = 0
        var data: List[Int] = Nil
        var state = t.initial
        while count < repeat * xs.length do {
          data = t.output(state) :: data
          state = t.next(state)
          count = count + 1
        }
        data.reverse ?= List.fill(repeat)(xs).flatten
    }
  }

  property("scanLeft produces cumulative results") {
    forAllNoShrink(Gen.listOf(Gen.choose(-100, 100))) { (xs: List[Int]) =>
      val t = Transducer.fromList(xs).scanLeft(0)(_ + _)
      t.toList ?= xs.scanLeft(0)(_ + _)
    }
  }

  property("Transducer.scanLeft produces expected output") {
    forAllNoShrink(Gen.posNum[Int]) { (start: Int) =>
      val t = Transducer.scanLeft(start)(x => x + 1)
      val s = t.toStream

      assertEquals(s.take(10).toList, List.tabulate(10)(i => i + start))
    }
  }

  property("Transducer.scanLeftUntil runs until the stop condition is met") {
    forAll(Gen.posNum[Int], Gen.posNum[Int]) { (start: Int, step: Int) =>
      val stop = start + step
      val t = Transducer.scanLeftUntil(start)(x => x + 1)(x => x >= stop)

      assertEquals(t.toList, List.tabulate(stop - start)(i => i + start))
    }
  }
}
