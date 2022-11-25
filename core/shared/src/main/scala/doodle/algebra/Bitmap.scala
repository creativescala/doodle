package doodle
package algebra

import java.io.File

trait Bitmap extends Algebra {

  /** Read an image from the given file
    */
  def read(file: File): Drawing[Unit]

  /** Convenience to read an image from the file specified in the given String
    */
  def read(file: String): Drawing[Unit] =
    read(new File(file))
}
