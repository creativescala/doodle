package doodle
package examples

import doodle.core._
import doodle.syntax._
import doodle.random._

import cats.syntax.cartesian._

object Windswept {

  def randomColor(meanHue: Angle) =
    for {
      hue <- Random.gaussian(meanHue.toDegrees, 10.0) map (_.degrees)
    } yield Color.hsl(hue, 0.8.normalized, 0.6.normalized)

  val leafGreen: Random[Color] = randomColor(80.degrees)
  val emeraldGreen: Random[Color] = randomColor(110.degrees)

  def randomSquare(color: Random[Color]): Random[Image] =
    for {
      fill <- color
    } yield Image.rectangle(50,50) fillColor fill lineWidth 0.0

  val leafSquare = randomSquare(leafGreen)
  val emeraldSquare = randomSquare(emeraldGreen)

  def randomColumn(one: Random[Image], two: Random[Image], three: Random[Image], four: Random[Image], five: Random[Image]): Random[Image] =
    (one |@| two |@| three |@| four |@| five) map { _ above _ above _ above _ above _ }

  val columnOne =
    randomColumn(emeraldSquare, emeraldSquare, emeraldSquare, emeraldSquare, emeraldSquare)

  val columnTwo =
    randomColumn(emeraldSquare, emeraldSquare, leafSquare, emeraldSquare, emeraldSquare)

  val columnThree =
    randomColumn(emeraldSquare, leafSquare, emeraldSquare, leafSquare, emeraldSquare)

  val columnFour =
    randomColumn(leafSquare, emeraldSquare, emeraldSquare, emeraldSquare, leafSquare)

  val singleRepeat: Random[Image] =
    (columnOne |@| columnTwo |@| columnThree |@| columnFour |@| columnThree |@| columnTwo) map { _ beside _ beside _ beside _ beside _ beside _ }

  val pattern: Random[Image] =
    (singleRepeat |@| singleRepeat |@| singleRepeat |@| columnOne) map { _ beside _ beside _ beside _ }

  def randomVec(x: Random[Double], y: Random[Double]): Random[Vec] =
    (x |@| y) map { (x, y) => Vec(x, y) }

  def gaussianVec(vec: Vec, stdDev: Double = 50): Random[Vec] =
    randomVec(Random.gaussian(vec.x, stdDev), Random.gaussian(vec.y, stdDev))

  def randomBezier(cp1: Vec, cp2: Vec, end: Vec, stdDev: Double): Random[BezierCurveTo] = {
    (gaussianVec(cp1, stdDev) |@| gaussianVec(cp2, stdDev) |@| gaussianVec(end, stdDev)) map {
      (cp1, cp2, end) => BezierCurveTo(cp1, cp2, end)
    }
  }

  val tendril: Random[Image] =
    for {
      hue     <- Random.gaussian(25, 15) map (_.degrees)
      stroke   = Color.hsla(hue, 0.8.normalized, 0.6.normalized, 0.6.normalized)
      start   <- gaussianVec(Vec(0,0), 10)
      curve1  <- randomBezier(Vec(-20, -400), Vec(200, -500), Vec(400, -100), 25)
      curve2  <- randomBezier(Vec(400, 50), Vec(200, 50), Vec(200, -200), 50)
    } yield Path(Seq(MoveTo(start), curve1, curve2)) lineColor stroke lineWidth 1.0

  val tendrils: Random[Image] =
    (1 to 800).foldLeft(tendril){ (randomImage, _) =>
      for {
        accum <- randomImage
        t     <- tendril
      } yield t on accum
    }

  def image =
    (for {
       t  <- tendrils
       p1 <- pattern
       p2 <- pattern
       p3 <- pattern
     } yield t on (p1 above p2 above p3)).run(scala.util.Random)
}
