package doodle
package examples

import doodle.core._
import doodle.random._
import doodle.syntax._
import doodle.chart._
import doodle.chart.syntax._
import doodle.chart.StandardInterpreter._
import cats.instances.list._
import cats.syntax.traverse._
import cats.syntax.cartesian._

object ScatterPlot {
  object normal {
    val point: Random[Point] =
      (Random.normal(0.0, 50.0) |@| Random.normal(0.0, 50.0)) map { (x,y) => Point(x,y) }

    val data: Random[List[Point]] =
      List.range(0, 100).map(_ => point).sequence

    val image = data.map(d => Chart.scatterPlot(d.toSeries)).run.asImage
  }

  object circle {
    val data: List[Point] =
      (for(a <- 0 to 360 by 5) yield { Point(1.0, a.degrees) }).toList
    val image = Chart.scatterPlot(data.toSeries).asImage
  }
}
