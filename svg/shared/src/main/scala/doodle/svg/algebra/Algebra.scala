package doodle
package svg
package algebra

import doodle.language.Basic
import doodle.algebra.generic._
import doodle.algebra.generic.reified._

final case object Algebra
  extends ReifiedLayout
  with ReifiedPath
  with ReifiedShape
  with GenericStyle[Reification]
  with GenericTransform[Reification]
  with Basic[Drawing]
