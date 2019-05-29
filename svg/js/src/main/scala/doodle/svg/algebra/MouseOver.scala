package doodle
package svg
package algebra

import scalatags.JsDom
import monix.reactive.Observable
import monix.reactive.subjects.PublishSubject

trait MouseOver extends doodle.interact.algebra.MouseOver[Drawing] {
  import JsDom.all._

  def mouseOver[A](img: Drawing[A]): (Drawing[A], Observable[Unit]) = {
    val subject = PublishSubject[Unit]()
    val callback = (_: Any) => {
      subject.onNext(())
    }

    val result =
      img.map{ case (bb, rdr) =>
        (bb,
         rdr.map{ case (tags, a) =>
           (tags(onmouseover:=callback) : JsDom.Tag, a)
         }
        )
      }

    (result, subject)
  }
}
