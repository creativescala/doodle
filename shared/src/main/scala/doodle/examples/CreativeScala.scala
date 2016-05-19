package doodle
package examples

import doodle.core._
import doodle.core.Image._
import doodle.syntax._

object CreativeScala {
  // Images from Creative Scala

  object paths {
    import doodle.core.Point._
    import doodle.core.Color._

    val triangle =
      List(
        LineTo(cartesian(50, 100)),
        LineTo(cartesian(100, 0)),
        LineTo(cartesian(0, 0))
      )

    val curve =
      List(BezierCurveTo(cartesian(50, 100), cartesian(100, 100), cartesian(150, 0)))

    def style(image: Image): Image =
      image.
        lineWidth(6.0).
        lineColor(royalBlue).
        fillColor(skyBlue)

    val openPaths =
      style(openPath(triangle) beside openPath(curve))

    val closedPaths =
      style(closedPath(triangle) beside closedPath(curve))

    val image = openPaths above closedPaths
  }

  object polygons {
    import doodle.core.Point._
    import doodle.core.Color._

    val triangle =
      closedPath(List(
                   MoveTo(polar(50, 0.degrees)),
                   LineTo(polar(50, 120.degrees)),
                   LineTo(polar(50, 240.degrees))
                 ))

    val square =
      closedPath(List(
                   MoveTo(polar(50, 45.degrees)),
                   LineTo(polar(50, 135.degrees)),
                   LineTo(polar(50, 225.degrees)),
                   LineTo(polar(50, 315.degrees))
                 ))

    val pentagon =
      closedPath((List(
                    MoveTo(polar(50, 72.degrees)),
                    LineTo(polar(50, 144.degrees)),
                    LineTo(polar(50, 216.degrees)),
                    LineTo(polar(50, 288.degrees)),
                    LineTo(polar(50, 360.degrees))
                  )))

    val spacer =
      rectangle(10, 100).noLine.noFill

    def style(image: Image): Image =
      image.lineWidth(6.0).lineColor(paleTurquoise).fillColor(turquoise)

    val image = style(triangle) beside spacer beside style(square) beside spacer beside style(pentagon)
  }

  object curvedPolygons {
    import doodle.core.Point._
    import doodle.core.Color._

    def curve(radius: Int, start: Angle, increment: Angle): PathElement = {
      BezierCurveTo(
        polar(radius *  .8, start + (increment * .3)),
        polar(radius * 1.2, start + (increment * .6)),
        polar(radius, start + increment)
      )
    }

    val triangle =
      closedPath(List(
                   MoveTo(polar(50, 0.degrees)),
                   curve(50, 0.degrees, 120.degrees),
                   curve(50, 120.degrees, 120.degrees),
                   curve(50, 240.degrees, 120.degrees)
                 ))

    val square =
      closedPath(List(
                   MoveTo(polar(50, 45.degrees)),
                   curve(50, 45.degrees, 90.degrees),
                   curve(50, 135.degrees, 90.degrees),
                   curve(50, 225.degrees, 90.degrees),
                   curve(50, 315.degrees, 90.degrees)
                 ))

    val pentagon =
      closedPath((List(
                    MoveTo(polar(50, 72.degrees)),
                    curve(50, 72.degrees, 72.degrees),
                    curve(50, 144.degrees, 72.degrees),
                    curve(50, 216.degrees, 72.degrees),
                    curve(50, 288.degrees, 72.degrees),
                    curve(50, 360.degrees, 72.degrees)
                  )))

    val spacer =
      rectangle(10, 100).noLine.noFill

    def style(image: Image): Image =
      image.lineWidth(6.0).lineColor(paleTurquoise).fillColor(turquoise)

    val image = style(triangle) beside spacer beside style(square) beside spacer beside style(pentagon)
  }


  object regularPolygons {
    import Point._

    def polygon(sides: Int, size: Int, initialRotation: Angle): Image = {
      def iter(n: Int, rotation: Angle): List[PathElement] =
        n match {
          case 0 =>
            Nil
          case n =>
            LineTo(polar(size, rotation * n + initialRotation)) :: iter(n - 1, rotation)
        }
      closedPath(MoveTo(polar(size, initialRotation)) :: iter(sides, 360.degrees / sides))
    }

    def style(img: Image): Image = {
      img.
        lineWidth(3.0).
        lineColor(Color.mediumVioletRed).
        fillColor(Color.paleVioletRed.fadeOut(0.5.normalized))
    }

    def makeShape(n: Int, increment: Int): Image =
        polygon(n+2, n * increment, 0.degrees)

    def makeColor(n: Int, spin: Angle, start: Color): Color =
      start spin (spin * n)

    val baseColor = Color.hsl(0.degrees, 0.7.normalized, 0.7.normalized)

    def makeImage(n: Int): Image = {
      n match {
        case 0 =>
          Image.empty
        case n =>
          val shape = makeShape(n, 10)
          val color = makeColor(n, 30.degrees, baseColor)
          makeImage(n-1) on (shape fillColor color)
      }
    }

    val image = makeImage(15)
  }


  object stars {
    import Point._

    def star(sides: Int, skip: Int, radius: Double): Image = {
      val rotation = 360.degrees * skip / sides

      val start = MoveTo(polar(radius, 0.degrees))
      val elements = (1 until sides).toList map { index =>
        val point = polar(radius, rotation * index)
        LineTo(point)
      }

      closedPath(start :: elements) lineWidth 2
    }

    def allBeside(imgs: List[Image]): Image =
      imgs match {
        case Nil => Image.empty
        case hd :: tl => hd beside allBeside(tl)
      }

    def allAbove(imgs: List[Image]): Image =
      imgs match {
        case Nil => Image.empty
        case hd :: tl => hd above allAbove(tl)
      }

    def style(img: Image, hue: Angle): Image =
      img.
        lineColor(Color.hsl(hue, 1.normalized, .25.normalized)).
        fillColor(Color.hsl(hue, 1.normalized, .75.normalized))

    def lineOfStars(n: Int, sides: Int) = {
      allBeside(
        (1 to n).toList map { skip =>
          star(sides, skip, 100)
        }
      )
    }

    def allStar =
      allAbove((3 to 33 by 2).toList map { sides =>
        allBeside((1 to sides/2).toList map { skip =>
          style(star(sides, skip, 20), 360.degrees * skip / sides)
        })
      })

    def line = lineOfStars(5, 10)
  }

  object randomConcentricCircles {
    import doodle.random._
    import cats.syntax.cartesian._

    val randomAngle: Random[Angle] =
      Random.double.map(x => x.turns)

    def randomColor(s: Normalized, l: Normalized): Random[Color] =
      randomAngle map (hue => Color.hsl(hue, s, l))

    def randomCircle(r: Double, color: Random[Color]): Random[Image] =
      color map (fill => Image.circle(r) fillColor fill)

    val randomPastel = randomColor(0.7.normalized, 0.7.normalized)

    def randomConcentricCircles(n: Int): Random[Image] =
      n match {
        case 0 => randomCircle(10, randomPastel)
        case n =>
          randomConcentricCircles(n-1) |@| randomCircle(n * 10, randomPastel) map {
            (circles, circle) => circles on circle
          }
      }
  }

  object sequentialBoxes {
    import doodle.random._
    import cats.syntax.cartesian._

    val randomAngle: Random[Angle] =
      Random.double.map(x => x.turns)

    val randomColor: Random[Color] =
      randomAngle map (hue => Color.hsl(hue, 0.7.normalized, 0.7.normalized))

    val randomSpin: Random[Double] =
      Random.normal(15.0, 10.0)

    def nextColor(color: Color): Random[Color] =
      randomSpin map { spin => color.spin(spin.degrees) }

    def coloredRectangle(color: Color): Image =
       rectangle(20, 20) fillColor color

    // Basic structural recursion
    def gradientBoxes(n: Int, color: Color): Image =
      n match {
        case 0 => coloredRectangle(color)
        case n => coloredRectangle(color) beside gradientBoxes(n-1, color.spin(15.degrees))
      }

    // Structural recursion with applicative
    def randomColorBoxes(n: Int): Random[Image] =
      n match {
        case 0 => randomColor map { c => coloredRectangle(c) }
        case n =>
          val box = randomColor map { c => coloredRectangle(c) }
          val boxes = randomColorBoxes(n-1)
          (box |@| boxes) map { (b, bs) => b beside bs }
      }

    // Structural recursion with applicative (with more pleasing result)
    def noisyGradientBoxes(n: Int, color: Color): Random[Image] =
      n match {
        case 0 => nextColor(color) map { c => coloredRectangle(c) }
        case n =>
          val box = nextColor(color) map { c => coloredRectangle(c) }
          val boxes = noisyGradientBoxes(n-1, color.spin(15.degrees))
          box |@| boxes map { (b, bs) =>  b beside bs }
      }

    // Structural recursion with monad
    def randomGradientBoxes(n: Int, color: Color): Random[Image] =
      n match {
        case 0 => Random.always(coloredRectangle(color))
        case n =>
          val box = coloredRectangle(color)
          val boxes = nextColor(color) flatMap { c => randomGradientBoxes(n-1, c) }
          boxes map { b => box beside b }
      }

    val image: Random[Image] = {
      val boxes = randomColor flatMap { c => randomGradientBoxes(4, c) }
      boxes |@| boxes |@| boxes map { (b1, b2, b3) => b1 above b2 above b3 }
    }
  }

  object scatterPlot {
    import doodle.random._
    import cats.syntax.cartesian._

    val normal = Random.normal(50, 15)
    val uniform = Random.natural(100).map(x => x.toDouble)
    val normalSquared = Random.normal map (x => (x * x * 7.5))

    def makePoint(x: Random[Double], y: Random[Double]): Random[Point] =
      x |@| y map { (x, y) => Point.cartesian(x, y) }

    val iter = (1 to 1000).toList

    def allOn(points: List[Random[Image]]): Random[Image] =
      points match {
        case Nil => Random.always(Image.empty)
        case img :: imgs => img |@| allOn(imgs) map { (i, is) => i on is }
      }

    val normal2D = makePoint(normal, normal)
    val uniform2D = makePoint(uniform, uniform)
    val normalSquared2D = makePoint(normalSquared, normalSquared)

    def point(loc: Point): Image =
      circle(2).fillColor(Color.cadetBlue.alpha(0.3.normalized)).noLine.at(loc.toVec)

    val spacer = rectangle(20, 20).noLine.noFill

    val image =
      (allOn(iter.map(i => uniform2D map (point _)))  |@|
       allOn(iter.map(i => normal2D map (point _))) |@|
       allOn(iter.map(i => normalSquared2D map (point _)))) map {
        (s1, s2, s3) => s1 beside spacer beside s2 beside spacer beside s3
      }
  }
}
