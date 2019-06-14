package doodle
package svg
package algebra

import scalatags.JsDom
import monix.reactive.Observable
import monix.reactive.subjects.ReplaySubject

trait MouseOver extends doodle.interact.algebra.MouseOver[Drawing] {
  import JsDom.all._

  var counter = 0

  def mouseOver[A](img: Drawing[A]): (Drawing[A], Observable[Unit]) = {
    counter = counter + 1
    val subject = ReplaySubject[Int]()
    val callback = (_: Any) => {
      println(s"callback invoked $counter")
      subject.onNext(counter)
      subject.onComplete()
    }

    val result =
      img.map{ case (bb, rdr) =>
        (bb,
         rdr.map{ case (tags, a) =>
           (tags(onmouseover:=callback) : JsDom.Tag, a)
         }
        )
      }

    import monix.eval.Task
    (result, subject.doOnNext(a => Task(println(s"got it $a"))).map(_ => ()))
  }
}
