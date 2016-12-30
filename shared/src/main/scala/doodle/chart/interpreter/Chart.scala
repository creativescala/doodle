package doodle
package chart
package interpreter

import cats.data.Reader
import doodle.core.Image
import doodle.chart.Axis.{Horizontal,Vertical}

object Chart {
  def interpreter[D <: DataType](chart: Chart[D]): Reader[Style,Image] = {
    import doodle.chart.Chart._
    chart match {
      case Numerical(s, x, y, t) =>
        // Here there is a bug in Scala's type inference. `s` should be inferred to have a higher-kinded existential type, but is instead inferred to have type `Any`. We propagate the mistake to get the code compiling.
        for {
          series <- Series.numerical[Any](s)
          min = s.min
          max = s.max
          xAxis <- Axis.numerical(x, Horizontal, min.x, max.x)
          yAxis <- Axis.numerical(x, Vertical, min.x, max.x)
        } yield xAxis on yAxis on series

      case Categorical(s, x, y, t) =>
        ???
    }
  }
}
