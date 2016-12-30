package doodle
package chart
package interpreter

object StandardInterpreter {
  implicit def chartInterpreter[D <: DataType]: Interpreter[D] =
    Chart.interpreter _
}
