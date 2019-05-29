package doodle
package svg
package effect

import doodle.core.Color
import monix.reactive.Observable
import monix.reactive.subjects.PublishSubject
import org.scalajs.dom

final case class Canvas(target: dom.Node,
                        size: Size,
                        background: Option[Color]) {

  val redraw: Observable[Int] = {
    val subject = PublishSubject[Int]()
    var started = false
    var lastTs = 0.0
    val callback: (Double => Unit) = (ts: Double) => {
      if(started) {
        subject.onNext((ts - lastTs).toInt)
      } else {
        subject.onNext(0)
        started = true
      }
      lastTs = ts
      ()
    }
    val register: (Unit => Unit) = _ => {
      val _ = dom.window.requestAnimationFrame(callback)
      ()
    }

    dom.window.requestAnimationFrame(callback.andThen(register))
    subject
  }

  def setSvg(svg: dom.Node): Unit = {
    val _ = target.appendChild(svg)
    ()
  }
}
object Canvas {
  def fromFrame(frame: Frame): Canvas = {
    val target = dom.document.getElementById(frame.id)
    Canvas(target, frame.size, frame.background)
  }
}
