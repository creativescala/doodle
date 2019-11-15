package doodle
package algebra

import java.io.File

trait Bitmap[F[_]] extends Algebra[F] {
  /**
   * Read an image from the given file
   */
  def read(file: File): F[Unit]

  /**
   * Convenience to read an image from the file specified in the given String
   */
  def read(file: String): F[Unit] =
    read(new File(file))
}
