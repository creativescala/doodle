package doodle
package svg

import doodle.algebra.generic.Finalized
import scalatags.generic.Bundle
import scala.collection.mutable

/**
  * Base trait for SVG implementations, defining common types
  *
  * Used for ML-style modules to ensure the JVM and JS implementations make
  * consistent use of types, and the compiler can prove this.
  */
trait Base {
  type Builder
  type FragT
  type Output <: FragT

  val bundle: Bundle[Builder, Output, FragT]

  type Tag = bundle.implicits.Tag
  type SvgResult[A] = (Tag, mutable.Set[Tag], A)
  type Drawing[A] = Finalized[SvgResult, A]
}
