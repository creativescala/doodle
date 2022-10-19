package doodle
package interact
package syntax

import cats.Monoid
import cats.effect.IO
import cats.effect.unsafe.IORuntime
import doodle.algebra.Algebra
import doodle.algebra.Picture
import doodle.interact.effect.AnimationWriter
import fs2.Stream

import java.io.File

trait AnimationWriterSyntax {
  def animationWriterNullCallback[A](r: Either[Throwable, A]): Unit =
    r match {
      case Left(err) =>
        println("There was an error rendering an animation")
        err.printStackTrace()

      case Right(_) => ()
    }

  implicit class AnimationWriterStreamOps[Alg <: Algebra, F[_], A](
      frames: Stream[IO, Picture[Alg, A]]
  ) {

    def writeToIO[Format] =
      new AnimationWriterIOOpsHelper[Format](frames)

    def write[Format] =
      new AnimationWriterOpsHelper[Format](frames)

    class AnimationWriterIOOpsHelper[Format](
        frames: Stream[IO, Picture[Alg, A]]
    ) {
      def apply[Frame](file: String, frame: Frame)(implicit
          m: Monoid[A],
          w: AnimationWriter[Alg, F, Frame, Format]
      ): IO[A] =
        apply(new File(file), frame)

      def apply[Frame](file: File, frame: Frame)(implicit
          m: Monoid[A],
          w: AnimationWriter[Alg, F, Frame, Format]
      ): IO[A] =
        w.write(file, frame, frames)
    }

    class AnimationWriterOpsHelper[Format](
        frames: Stream[IO, Picture[Alg, A]]
    ) {
      def apply[Frame](file: String, frame: Frame)(implicit
          m: Monoid[A],
          w: AnimationWriter[Alg, F, Frame, Format],
          runtime: IORuntime
      ): Unit =
        apply(new File(file), frame)

      def apply[Frame](file: File, frame: Frame)(implicit
          m: Monoid[A],
          w: AnimationWriter[Alg, F, Frame, Format],
          runtime: IORuntime
      ): Unit =
        w.write(file, frame, frames).unsafeRunAsync(animationWriterNullCallback)
    }
  }
}
