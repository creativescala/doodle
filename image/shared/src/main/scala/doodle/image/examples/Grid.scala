package doodle
package image
package examples

import cats.implicits._
import doodle.image.syntax.all._
import doodle.random._

/** Grid patterns in the style of Sasj's geometric series such as
  * https://www.instagram.com/p/BohZM4RiWJk/?taken-by=sasj_nl
  */
object Grid {
  def row(size: Int)(image: Random[Image]): Random[Image] =
    (0 until size).map(_ => image).toList.sequence.map(_.allBeside)

  def square(size: Int)(image: Random[Image]): Random[Image] =
    (0 until size).map(_ => row(size)(image)).toList.sequence.map(_.allAbove)
}
