package doodle
package svg
package algebra

import cats.data.Writer
import doodle.language.Basic
import doodle.algebra.generic._
import doodle.algebra.generic.reified._

final case object Algebra
  extends ReifiedLayout
  with ReifiedPath
  with ReifiedShape
  with GenericStyle[Writer[List[Reified],?]]
  with GenericTransform[Writer[List[Reified],?]]
  with Basic[Drawing]
