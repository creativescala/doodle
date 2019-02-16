package doodle
package svg
package algebra

import doodle.language.Basic
import doodle.algebra.generic._

final case object Algebra
  extends GenericLayout
  with GenericPath
  with GenericShape
  with GenericStyle
  with GenericTransform
  with Basic[Drawing]
