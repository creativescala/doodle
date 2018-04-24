/*
 * Copyright 2015 noelwelsh
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
package fx
package engine

import cats.effect.IO
import doodle.algebra.generic.{DrawingContext, Transform}
import doodle.core.Point
import doodle.engine.{Frame, Size}
import doodle.fx.Drawing
import doodle.fx.algebra.Algebra
import javafx.application.{Application, Platform}
import javafx.stage.Stage
import javafx.scene.{Group, Scene}
import javafx.scene.canvas.Canvas
import javafx.animation.AnimationTimer
import scala.concurrent.SyncVar
import scala.util.control.NonFatal
// import scala.annotation.tailrec

class Engine extends Application {
  def start(stage: Stage): Unit = {
    import Size._

    val timer = new AnimationTimer {
      def draw[A](request: FrameRequest[A]): Unit = {
        try {
          val (bb, ctx) = request.drawing(DrawingContext.default)
          val frame = request.frame

          val root = new Group()
          val scene: Scene =
            frame.size match {
              case FitToImage(b) =>
                new Scene(root, bb.width + b, bb.height + b)

              case FixedSize(w, h) =>
                new Scene(root, w, h)

              case FullScreen =>
                stage.setFullScreen(true)
                new Scene(root)
            }
          stage.setScene(scene)
          stage.setTitle(frame.title)

          val canvas: Canvas =
            new Canvas(scene.getWidth(), scene.getHeight())
          val gc = canvas.getGraphicsContext2D()
          val tx = Transform.logicalToScreen(scene.getWidth(), scene.getHeight())

          request.cb(Right(ctx((gc, tx))(Point.zero).unsafeRunSync))

          root.getChildren.add(canvas)
          stage.show()
        } catch {
          case NonFatal(e) => request.cb(Left(e))
        }
      }

      def handle(now: Long): Unit = {
        try {
          val request: FrameRequest[A] forSome {type A} = FrameRequest.take()
          draw(request)
        } catch {
          case _: NoSuchElementException => ()
        }
      }
    }

    timer.start()
  }
}

final case class FrameRequest[A](frame: Frame, drawing: Drawing[A], cb: Either[Throwable,A] => Unit)
object FrameRequest {
  val channel: SyncVar[FrameRequest[_]] = new SyncVar()

  def put(request: FrameRequest[_]): Unit =
    channel.put(request)

  def take(timeout: Long = 0L): FrameRequest[_] =
    channel.take(timeout)
}

object Engine {
  def frame[A](frame: Frame)(f: Algebra => Drawing[A]): IO[A] = {
    start()
    def cbHandler(cb: Either[Throwable, A] => Unit): Unit = {
      // Assume the point at which we receive the callback is the point when the
      // effect is being run. Hence this is the point when we ask the
      // Engine to render.
      // println("Constructing FrameRequest")
      FrameRequest.put(FrameRequest(frame, f(Algebra()), cb))
      // println("FrameRequest has been put")
    }
    IO.async(cbHandler)
  }

  private var started: Boolean = false
  def start(): Unit = {
    if(started) ()
    else {
      // Don't exit when no windows are open---we might open some more.
      Platform.setImplicitExit(false)
      val runner = new Thread("JavaFX runner") {
        override def run(): Unit =
          Application.launch(classOf[Engine])
      }
      runner.start()
      started = true
    }
  }

  def stop(): Unit = {
    Platform.exit()
    started = false
  }
}
