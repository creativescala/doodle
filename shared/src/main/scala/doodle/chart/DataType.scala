package doodle
package chart

/** `DataType` phantom type represents a constraint on the data being charted. */
sealed trait DataType
object DataType {
  sealed trait Numerical extends DataType
  sealed trait Categorical extends DataType
}
