package doodle
package examples

import doodle.core._
import doodle.syntax._
import doodle.random._

import cats.syntax.cartesian._

object Windswept {
  def randomVec(x: Random[Double], y: Random[Double]): Random[Vec] =
    (x |@| y) map { (x, y) => Vec(x, y) }

  def gaussianVec(vec: Vec, stdDev: Double = 50): Random[Vec] =
    randomVec(Random.gaussian(vec.x, stdDev), Random.gaussian(vec.y, stdDev))

  def randomBezier(cp1: Vec, cp2: Vec, end: Vec, stdDev: Double): Random[BezierCurveTo] = {
    (gaussianVec(cp1, stdDev) |@| gaussianVec(cp2, stdDev) |@| gaussianVec(end, stdDev)) map {
      (cp1, cp2, end) => BezierCurveTo(cp1, cp2, end)
    }
  }

  val tendril =
    for {
      hue     <- Random.gaussian(25, 15) map (_.degrees)
      stroke   = Color.hsla(hue, 0.8.normalized, 0.6.normalized, 0.6.normalized)
      start   <- gaussianVec(Vec(0,0), 10)
      curve1  <- randomBezier(Vec(-20, -400), Vec(200, -500), Vec(400, -100), 25)
      curve2  <- randomBezier(Vec(400, 50), Vec(200, 50), Vec(200, -200), 50)
    } yield Path(Seq(MoveTo(start), curve1, curve2)) lineColor stroke lineWidth 1.0

  def tendrils =
    for(i <- 1 to 800) yield tendril.run(scala.util.Random)

  def image =
    tendrils.foldLeft(Image.empty){ (image, tendril) => tendril on image }
}
