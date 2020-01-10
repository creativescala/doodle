package doodle
package interact
package animation

import cats.implicits._
import doodle.interact.syntax._
import org.scalacheck._
import org.scalacheck.Prop._

object EnumeratorSpec extends Properties("Enumerator properties") {
  property("upto empty range produces no output") = forAllNoShrink {
    (x: Double) =>
      x.upTo(x).forSteps(10).toList ?= List()
  }

  property("uptoIncluding empty range produces single output") =
    forAllNoShrink { (x: Double) =>
      x.upToIncluding(x).forSteps(10).toList ?= List(x)
    }

  property("upto produces requested number of steps when range is not empty") =
    forAllNoShrink(
      Gen.choose(-100.0, 100.0) :| "Start",
      Gen.posNum[Double] :| "Difference",
      Gen.choose(1L, 100L) :| "Steps"
    ) { (start, difference, steps) =>
      val t = start.upTo(start + difference).forSteps(steps)
      t.toList.length ?= steps.toInt
    }

  property(
    "uptoIncluding produces requested number of steps when range is not empty"
  ) = forAllNoShrink(
    Gen.choose(-100.0, 100.0) :| "Start",
    Gen.posNum[Double] :| "Difference",
    Gen.choose(1L, 100L) :| "Steps"
  ) { (start, difference, steps) =>
    val t = start.upToIncluding(start + difference).forSteps(steps)
    t.toList.length ?= steps.toInt
  }

  property("uptoIncluding ends on stop") = forAllNoShrink(
    Gen.choose(-100.0, 100.0) :| "Start",
    Gen.posNum[Double] :| "Difference",
    Gen.choose(1L, 100L) :| "Steps"
  ) { (start, difference, steps) =>
    val t = start.upToIncluding(start + difference).forSteps(steps)
    t.toList.last ?= (start + difference)
  }
}
