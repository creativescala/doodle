package doodle
package golden

import cats.implicits._
import doodle.java2d._
import doodle.syntax._
import munit._

class Path extends FunSuite with GoldenPicture {

  testPicture("path-polygons") {
    regularPolygon[Algebra, Drawing](4, 100, 0.degrees)
      .beside(regularPolygon[Algebra, Drawing](5, 100, 0.degrees))
      .beside(regularPolygon[Algebra, Drawing](7, 100, 0.degrees))
      .beside(regularPolygon[Algebra, Drawing](20, 100, 0.degrees))
  }

  testPicture("path-stars") {
    star[Algebra, Drawing](4, 100, 50, 0.degrees)
      .beside(star[Algebra, Drawing](5, 100, 50, 0.degrees))
      .beside(star[Algebra, Drawing](7, 100, 50, 0.degrees))
      .beside(star[Algebra, Drawing](20, 100, 50, 0.degrees))
  }

  testPicture("path-rounded-rectangle") {
    roundedRectangle[Algebra, Drawing](100, 50, 10)
      .beside(roundedRectangle[Algebra, Drawing](100, 100, 15))
      .beside(roundedRectangle[Algebra, Drawing](50, 100, 20))
      .beside(roundedRectangle[Algebra, Drawing](100, 100, 0))
  }

  testPicture("path-equilateral-triangle") {
    equilateralTriangle[Algebra, Drawing](100)
      .beside(equilateralTriangle[Algebra, Drawing](150))
      .beside(equilateralTriangle[Algebra, Drawing](50))
      .beside(equilateralTriangle[Algebra, Drawing](200))
  }
}
