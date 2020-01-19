package doodle
package interact
package animation

import cats.implicits._
import org.scalacheck._
import org.scalacheck.Prop._

object TransducerSpec extends Properties("Transducer properties") {
  property("empty produces no output") =
    Transducer.empty[Int].toList ?= List.empty[Int]

  property("pure produces just one output") = forAllNoShrink { (x: Int) =>
    Transducer.pure(x).toList ?= List(x)
  }

  property("toList generates all elements from the transducer") =
    forAllNoShrink { (elts: List[Int]) =>
      Transducer(elts: _*).toList ?= elts
    }

  property("product produces shortest pairs") = forAllNoShrink {
    (xs: List[Int], ys: List[Int]) =>
      Transducer(xs: _*).product(Transducer(ys: _*)).toList ?= xs.zip(ys)
  }

  property("++ combines transducers in series") = forAllNoShrink {
    (xs: List[Int], ys: List[Int]) =>
      val t = Transducer.fromList(xs) ++ (Transducer.fromList(ys))

      t.toList ?= (xs ++ ys)
  }

  property("and combines transducers in parallel") = forAllNoShrink {
    (xs: List[Int], ys: List[Int]) =>
      val (shortest, longest) =
        if (xs.length < ys.length) (xs, ys) else (ys, xs)

      val pad = List.fill(longest.length - shortest.length)(0)
      val a = Transducer.fromList(shortest ++ pad)
      val b = Transducer.fromList(longest)

      a.and(b).toList ?= (shortest ++ pad).zip(longest).map {
        case (a, b) => a + b
      }
  }

  property("and retains last value of first transducer to stop") =
    forAllNoShrink { (x: Int, xs: List[Int]) =>
      val a = Transducer.fromList(x :: xs)
      val b = Transducer.pure(0)

      a.and(b).toList ?= (x :: xs)
    }
}
