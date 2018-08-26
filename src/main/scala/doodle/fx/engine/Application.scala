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
import doodle.algebra.DrawingContext
import doodle.fx.algebra.{Algebra,WithContext}
import javafx.application.{Application => FxApplication, Platform}
import javafx.geometry.Point2D
import javafx.stage.Stage
import javafx.scene.{Group, Scene}
import javafx.scene.canvas.{Canvas, GraphicsContext}
import javafx.scene.effect.BlendMode
import javafx.scene.paint.{Color => FxColor}
import javafx.animation.AnimationTimer
import scala.concurrent.SyncVar
// import scala.annotation.tailrec

class Application extends FxApplication {
  def start(stage: Stage): Unit = {
    import Size._

    val timer = new AnimationTimer {
      def handle(now: Long): Unit = {
        try {
          val request = FrameRequest.take()
          val frame = request.frame
          val root = new Group()
          val scene: Scene =
            frame.size match {
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
          val context = canvas.getGraphicsContext2D()
          val transform =
            Transform.logicalToScreen(scene.getWidth(), scene.getHeight())

          request.cb(context, transform)

          root.getChildren.add(canvas)
          stage.show()
        } catch {
          case _: NoSuchElementException => ()
        }
      }
    }

    timer.start()
  }
}

final case class FrameRequest(frame: Frame,
                              cb: (GraphicsContext, Transform.Transform) => Unit)
object FrameRequest {
  val channel: SyncVar[FrameRequest] = new SyncVar()

  def put(request: FrameRequest): Unit =
    channel.put(request)

  def take(timeout: Long = 0L): FrameRequest =
    channel.take(timeout)
}

object Application {
  def frame[A](frame: Frame)(f: Algebra => WithContext[A]): IO[A] = {
    start()
    def cbHandler(cb: Either[Throwable, A] => Unit): Unit = {
      // Assume the point at which we receive the callback is the point when the
      // effect is being run. Hence this is the point when we ask the
      // Application to render.
      val requestCallback: (GraphicsContext, Transform.Transform) => Unit =
        (context, tx) => {
          // println("Context received. Applying.")
          val a =
            f(Algebra()).
              run((context, DrawingContext.empty[BlendMode,FxColor], tx)).
              map{ case (_, next) => next.run(tx(new Point2D(0,0))) }.
              value.
              unsafeRunSync()
          cb(Right(a))
        }

      // println("Constructing FrameRequest")
      FrameRequest.put(FrameRequest(frame, requestCallback))
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
          FxApplication.launch(classOf[Application])
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
