package doodle
package interact
package syntax

import cats.Monoid
import cats.effect.IO
import doodle.algebra.{Algebra, Picture}
import doodle.interact.effect.AnimationWriter
import java.io.File
import monix.execution.Scheduler
import monix.reactive.{Observable, ObservableLike}

trait AnimationWriterSyntax {
  def animationWriterNullCallback[A](r: Either[Throwable, A]): Unit =
    r match {
      case Left(err) =>
        println("There was an error rendering an animation")
        err.printStackTrace()

      case Right(_) => ()
    }

  implicit class AnimationWriterObservableOps[Alg[x[_]] <: Algebra[x], F[_], A](
      frames: Observable[Picture[Alg, F, A]]) {

    def writeToIO[Format] =
      new AnimationWriterIOOpsHelper[Format](frames)

    def write[Format] =
      new AnimationWriterOpsHelper[Format](frames)

    class AnimationWriterIOOpsHelper[Format](
        frames: Observable[Picture[Alg, F, A]]) {
      def apply[Frame](file: String, frame: Frame)(
          implicit s: Scheduler,
          m: Monoid[A],
          w: AnimationWriter[Alg, F, Frame, Format]): IO[A] =
        apply(new File(file), frame)

      def apply[Frame](file: File, frame: Frame)(
          implicit s: Scheduler,
          m: Monoid[A],
          w: AnimationWriter[Alg, F, Frame, Format]): IO[A] =
        w.write(file, frame, frames)
    }

    class AnimationWriterOpsHelper[Format](
        frames: Observable[Picture[Alg, F, A]]) {
      def apply[Frame](file: String, frame: Frame)(
          implicit s: Scheduler,
          m: Monoid[A],
          w: AnimationWriter[Alg, F, Frame, Format]): Unit =
        apply(new File(file), frame)

      def apply[Frame](file: File, frame: Frame)(
          implicit s: Scheduler,
          m: Monoid[A],
          w: AnimationWriter[Alg, F, Frame, Format]): Unit =
        w.write(file, frame, frames).unsafeRunAsync(nullCallback)
    }
  }

  implicit class AnimationWriterObservableLikeOps[Alg[x[_]] <: Algebra[x], F[_],
  G[_], A](frames: G[Picture[Alg, F, A]]) {

    def writeFramesToIO[Format](implicit o: ObservableLike[G]) =
      o(frames).writeToIO[Format]

    def writeFrames[Format](implicit o: ObservableLike[G]) =
      o(frames).write[Format]
  }
}
