package doodle
package examples

import doodle.core._
import doodle.core.Image._
import doodle.syntax._
import doodle.backend.StandardInterpreter._

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

    def randomAngle: Random[Angle] =
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
}
