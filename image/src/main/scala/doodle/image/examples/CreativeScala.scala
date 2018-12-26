package doodle
package image
package examples

import doodle.core._
import doodle.syntax._
import doodle.image.Image._

object CreativeScala {
  // Images from Creative Scala

  object paths {
    import doodle.core.Point._
    import doodle.core.PathElement._
    import doodle.core.Color._

    val triangle =
      List(
        lineTo(cartesian(50, 100)),
        lineTo(cartesian(100, 0)),
        lineTo(cartesian(0, 0))
      )

    val curve =
      List(curveTo(cartesian(50, 100), cartesian(100, 100), cartesian(150, 0)))

    def style(image: Image): Image =
      image.strokeWidth(6.0).strokeColor(royalBlue).fillColor(skyBlue)

    val openPaths =
      style(openPath(triangle) beside openPath(curve))

    val closedPaths =
      style(closedPath(triangle) beside closedPath(curve))

    val image = openPaths above closedPaths
  }

  object polygons {
    import doodle.core.Point._
    import doodle.core.PathElement._
    import doodle.core.Color._

    val triangle =
      closedPath(
        List(
          moveTo(polar(50, 0.degrees)),
          lineTo(polar(50, 120.degrees)),
          lineTo(polar(50, 240.degrees))
        ))

    val square =
      closedPath(
        List(
          moveTo(polar(50, 45.degrees)),
          lineTo(polar(50, 135.degrees)),
          lineTo(polar(50, 225.degrees)),
          lineTo(polar(50, 315.degrees))
        ))

    val pentagon =
      closedPath(
        (
          List(
            moveTo(polar(50, 72.degrees)),
            lineTo(polar(50, 144.degrees)),
            lineTo(polar(50, 216.degrees)),
            lineTo(polar(50, 288.degrees)),
            lineTo(polar(50, 360.degrees))
          )
        ))

    val spacer =
      rectangle(10, 100).noStroke.noFill

    def style(image: Image): Image =
      image.strokeWidth(6.0).strokeColor(paleTurquoise).fillColor(turquoise)

    val image = style(triangle) beside spacer beside style(square) beside spacer beside style(
      pentagon)
  }

  object curvedPolygons {
    import doodle.core.Point._
    import doodle.core.PathElement._
    import doodle.core.Color._

    def curve(radius: Int, start: Angle, increment: Angle): PathElement = {
      BezierCurveTo(
        polar(radius * .8, start + (increment * .3)),
        polar(radius * 1.2, start + (increment * .6)),
        polar(radius.toDouble, start + increment)
      )
    }

    val triangle =
      closedPath(
        List(
          moveTo(polar(50, 0.degrees)),
          curve(50, 0.degrees, 120.degrees),
          curve(50, 120.degrees, 120.degrees),
          curve(50, 240.degrees, 120.degrees)
        ))

    val square =
      closedPath(
        List(
          moveTo(polar(50, 45.degrees)),
          curve(50, 45.degrees, 90.degrees),
          curve(50, 135.degrees, 90.degrees),
          curve(50, 225.degrees, 90.degrees),
          curve(50, 315.degrees, 90.degrees)
        ))

    val pentagon =
      closedPath(
        (
          List(
            moveTo(polar(50, 72.degrees)),
            curve(50, 72.degrees, 72.degrees),
            curve(50, 144.degrees, 72.degrees),
            curve(50, 216.degrees, 72.degrees),
            curve(50, 288.degrees, 72.degrees),
            curve(50, 360.degrees, 72.degrees)
          )
        ))

    val spacer =
      rectangle(10, 100).noStroke.noFill

    def style(image: Image): Image =
      image.strokeWidth(6.0).strokeColor(paleTurquoise).fillColor(turquoise)

    val image = style(triangle) beside spacer beside style(square) beside spacer beside style(
      pentagon)
  }

  object regularPolygons {
    import Point._
    import PathElement._

    def polygon(sides: Int, size: Int, initialRotation: Angle): Image = {
      def iter(n: Int, rotation: Angle): List[PathElement] =
        n match {
          case 0 =>
            Nil
          case n =>
            lineTo(polar(size.toDouble,
                         rotation * n.toDouble + initialRotation)) :: iter(
              n - 1,
              rotation)
        }
      closedPath(
        moveTo(polar(size.toDouble, initialRotation)) :: iter(
          sides,
          360.degrees / sides.toDouble))
    }

    def style(img: Image): Image = {
      img
        .strokeWidth(3.0)
        .strokeColor(Color.mediumVioletRed)
        .fillColor(Color.paleVioletRed.fadeOut(0.5.normalized))
    }

    def makeShape(n: Int, increment: Int): Image =
      polygon(n + 2, n * increment, 0.degrees)

    def makeColor(n: Int, spin: Angle, start: Color): Color =
      start spin (spin * n.toDouble)

    val baseColor = Color.hsl(0.degrees, 0.7, 0.7)

    def makeImage(n: Int): Image = {
      n match {
        case 0 =>
          Image.empty
        case n =>
          val shape = makeShape(n, 10)
          val color = makeColor(n, 30.degrees, baseColor)
          makeImage(n - 1) on (shape fillColor color)
      }
    }

    val image = makeImage(15)
  }

  object stars {
    import Point._
    import PathElement._

    def star(sides: Int, skip: Int, radius: Double): Image = {
      val rotation = 360.degrees * skip.toDouble / sides.toDouble

      val start = moveTo(polar(radius, 0.degrees))
      val elements = (1 until sides).toList map { index =>
        val point = polar(radius, rotation * index.toDouble)
        lineTo(point)
      }

      closedPath(start :: elements) strokeWidth 2
    }

    def allBeside(imgs: List[Image]): Image =
      imgs match {
        case Nil      => Image.empty
        case hd :: tl => hd beside allBeside(tl)
      }

    def allAbove(imgs: List[Image]): Image =
      imgs match {
        case Nil      => Image.empty
        case hd :: tl => hd above allAbove(tl)
      }

    def style(img: Image, hue: Angle): Image =
      img.strokeColor(Color.hsl(hue, 1, .25)).fillColor(Color.hsl(hue, 1, .75))

    def strokeOfStars(n: Int, sides: Int) = {
      allBeside(
        (1 to n).toList map { skip =>
          star(sides, skip, 100)
        }
      )
    }

    def allStar =
      allAbove((3 to 33 by 2).toList map { sides =>
        allBeside((1 to sides / 2).toList map { skip =>
          style(star(sides, skip, 20),
                360.degrees * skip.toDouble / sides.toDouble)
        })
      })

    def stroke = strokeOfStars(5, 10)
  }

  object randomConcentricCircles {
    import doodle.random._

    val randomAngle: Random[Angle] =
      Random.double.map(x => x.turns)

    def randomColor(s: Double, l: Double): Random[Color] =
      randomAngle map (hue => Color.hsl(hue, s, l))

    def randomCircle(r: Double, color: Random[Color]): Random[Image] =
      color map (fill => Image.circle(r) fillColor fill)

    val randomPastel = randomColor(0.7, 0.7)

    def randomConcentricCircles(count: Int, size: Int): Random[Image] =
      count match {
        case 0 => Random.always(Image.empty)
        case n =>
          randomCircle(size.toDouble, randomPastel).flatMap { circle =>
            randomConcentricCircles(n - 1, size + 5) map { circles =>
              circle on circles
            }
          }
      }
  }

  object sequentialBoxes {
    import doodle.random._
    import cats.syntax.all._

    val randomAngle: Random[Angle] =
      Random.double.map(x => x.turns)

    val randomColor: Random[Color] =
      randomAngle map (hue => Color.hsl(hue, 0.7, 0.7))

    val randomSpin: Random[Double] =
      Random.normal(15.0, 10.0)

    def nextColor(color: Color): Random[Color] =
      randomSpin map { spin =>
        color.spin(spin.degrees)
      }

    def coloredRectangle(color: Color, size: Int = 40): Image =
      rectangle(size.toDouble, size.toDouble)
        .strokeWidth(5.0)
        .strokeColor(color.spin(30.degrees))
        .fillColor(color)

    // Basic structural recursion
    def sequentialBoxes(n: Int, color: Color): Image =
      n match {
        case 0 => Image.empty
        case n => coloredRectangle(color) beside sequentialBoxes(n - 1, color)
      }

    // Simple variant on structural recursion
    def stackedBoxes(n: Int, color: Color): Image =
      n match {
        case 0 => Image.empty
        case n => coloredRectangle(color) above stackedBoxes(n - 1, color)
      }

    // Basic structural recursion with auxillary parameter
    def growingBoxes(count: Int, size: Int): Image =
      count match {
        case 0 => Image.empty
        case n =>
          coloredRectangle(Color.royalBlue, size) beside growingBoxes(n - 1,
                                                                      size + 10)
      }

    // Basic structural recursion modifying both parameters
    def gradientBoxes(n: Int, color: Color): Image =
      n match {
        case 0 => Image.empty
        case n =>
          coloredRectangle(color) beside gradientBoxes(n - 1,
                                                       color.spin(15.degrees))
      }

    // Structural recursion with applicative (implemented via monad)
    def randomColorBoxes(count: Int): Random[Image] =
      count match {
        case 0 => Random.always(Image.empty)
        case n =>
          val box = randomColor map { c =>
            coloredRectangle(c)
          }
          val boxes = randomColorBoxes(n - 1)
          box flatMap { b =>
            boxes map { bs =>
              b beside bs
            }
          }
      }

    // Structural recursion with applicative (with sometimes more pleasing result)
    def noisyGradientBoxes(n: Int, color: Color): Random[Image] =
      n match {
        case 0 => Random.always(Image.empty)
        case n =>
          val box = nextColor(color) map { c =>
            coloredRectangle(c)
          }
          val boxes = noisyGradientBoxes(n - 1, color.spin(15.degrees))
          (box, boxes) mapN { (b, bs) =>
            b beside bs
          }
      }

    // Structural recursion with monad
    def randomGradientBoxes(n: Int, color: Color): Random[Image] =
      n match {
        case 0 => Random.always(Image.empty)
        case n =>
          val box = coloredRectangle(color)
          val boxes = nextColor(color) flatMap { c =>
            randomGradientBoxes(n - 1, c)
          }
          boxes map { b =>
            box beside b
          }
      }

    // Image written out as one big expression
    val expression =
      (
        Image
          .rectangle(40, 40)
          .strokeWidth(5.0)
          .strokeColor(Color.royalBlue.spin(30.degrees))
          .fillColor(Color.royalBlue) beside
          Image
            .rectangle(40, 40)
            .strokeWidth(5.0)
            .strokeColor(Color.royalBlue.spin(30.degrees))
            .fillColor(Color.royalBlue) beside
          Image
            .rectangle(40, 40)
            .strokeWidth(5.0)
            .strokeColor(Color.royalBlue.spin(30.degrees))
            .fillColor(Color.royalBlue) beside
          Image
            .rectangle(40, 40)
            .strokeWidth(5.0)
            .strokeColor(Color.royalBlue.spin(30.degrees))
            .fillColor(Color.royalBlue) beside
          Image
            .rectangle(40, 40)
            .strokeWidth(5.0)
            .strokeColor(Color.royalBlue.spin(30.degrees))
            .fillColor(Color.royalBlue)
    )

    // Image written out as one expression using abstraction (a name)
    val abstraction = {
      val box =
        Image
          .rectangle(40, 40)
          .strokeWidth(5.0)
          .strokeColor(Color.royalBlue.spin(30.degrees))
          .fillColor(Color.royalBlue)

      box beside box beside box beside box beside box
    }

    val image: Random[Image] = {
      val boxes = randomColor flatMap { c =>
        randomGradientBoxes(4, c)
      }
      (boxes, boxes, boxes) mapN { (b1, b2, b3) =>
        b1 above b2 above b3
      }
    }
  }

  object scatterPlot {
    import doodle.random._
    import cats.syntax.all._

    val normal = Random.normal(50, 15)
    val uniform = Random.natural(100).map(x => x.toDouble)
    val normalSquared = Random.normal map (x => (x * x * 7.5))

    def makePoint(x: Random[Double], y: Random[Double]): Random[Point] =
      (x, y) mapN { (x, y) =>
        Point.cartesian(x, y)
      }

    val iter = (1 to 1000).toList

    def allOn(points: List[Random[Image]]): Random[Image] =
      points match {
        case Nil => Random.always(Image.empty)
        case img :: imgs =>
          (img, allOn(imgs)) mapN { (i, is) =>
            i on is
          }
      }

    val normal2D = makePoint(normal, normal)
    val uniform2D = makePoint(uniform, uniform)
    val normalSquared2D = makePoint(normalSquared, normalSquared)

    def point(loc: Point): Image =
      circle(2)
        .fillColor(Color.cadetBlue.alpha(0.3.normalized))
        .noStroke
        .at(loc.toVec)

    val spacer = rectangle(20, 20).noStroke.noFill

    val image =
      (allOn(iter.map(_ => uniform2D map (point _))),
       allOn(iter.map(_ => normal2D map (point _))),
       allOn(iter.map(_ => normalSquared2D map (point _)))) mapN {
        (s1, s2, s3) =>
          s1 beside spacer beside s2 beside spacer beside s3
      }
  }

  object parametricNoise {
    import doodle.random._
    import cats.syntax.all._

    def rose(k: Int): Angle => Point =
      (angle: Angle) => {
        Point.cartesian((angle * k.toDouble).cos * angle.cos,
                        (angle * k.toDouble).cos * angle.sin)
      }

    def scale(point: Point): Point =
      Point.polar(point.r * 400, point.angle)

    def perturb(point: Point): Random[Point] =
      (Random.normal(10.0, 10.0), Random.normal(10.0, 10.0)) mapN { (x, y) =>
        Point.cartesian(point.x + x, point.y + y)
      }

    def randomCircle(point: Point, hue: Angle): Random[Image] = {
      val at = perturb(point) map (pt => pt.toVec)
      val size = Random.natural(5) map (r => r + 5)
      val lightness = Random.double
      val alpha = Random.double

      (size, lightness, alpha, at) mapN { (r, l, a, at) =>
        val fill = Color.hsla(hue, l, 0.4, a)
        circle(r.toDouble).noFill.strokeColor(fill).at(at)
      }
    }

    def perturbedRose(k: Int, hue: Angle): Angle => Random[Image] =
      rose(k) andThen scale andThen { pt =>
        randomCircle(pt, hue)
      }

    def allOn(points: List[Random[Image]]): Random[Image] =
      points match {
        case Nil => Random.always(Image.empty)
        case img :: imgs =>
          (img, allOn(imgs)) mapN { (i, is) =>
            i on is
          }
      }

    val image: Random[Image] =
      allOn(
        (3 to 7 by 2).toList map { k =>
          val r = perturbedRose(k, ((k - 3) * 30).degrees)
          allOn(
            (1 to 360).toList map { d =>
              val angle = d.degrees
              r(angle)
            }
          )
        }
      )
  }

  object cross {
    val circle = Image.circle(20)

    def basic(n: Int): Image =
      n match {
        case 0 =>
          circle
        case n =>
          circle beside (circle above basic(n - 1) above circle) beside circle
      }

    def shrinking(n: Int): Image =
      n match {
        case 0 =>
          Image.circle(40)
        case n =>
          val circle = Image.circle(40 * (1.0 / (n + 1)))
          circle beside (circle above shrinking(n - 1) above circle) beside circle
      }
  }

  object sierpinski {
    val triangle = Image.triangle(10, 10) strokeColor Color.magenta

    def sierpinski(count: Int): Image =
      count match {
        case 0 => triangle above (triangle beside triangle)
        case n =>
          val unit = sierpinski(n - 1)
          unit above (unit beside unit)
      }
  }

  object point {
    val point = Image.circle(5).at(40, 40).fillColor(Color.red).noStroke
    val spacer = Image.square(5).noStroke.noFill
    val xAxis = Image.line(40, 0) above spacer //above Image.text("x")
    val yAxis = Image.line(0, 40) beside spacer //beside Image.text("y")
    val cartesian = xAxis on yAxis on point
  }

  object parametricCircle {
    def parametricCircle(angle: Angle): Point =
      Point.polar(100, angle)

    def rose(k: Int): Angle => Point =
      (angle: Angle) => {
        Point.cartesian((angle * k.toDouble).cos * angle.cos,
                        (angle * k.toDouble).cos * angle.sin)
      }

    val dot = Image
      .circle(5)
      .fillColor(Color.crimson.spin(15.degrees).desaturate(0.4.normalized))
      .strokeColor(Color.crimson)
      .strokeWidth(3)

    def sample(f: Angle => Point, samples: Int, marker: Image = dot): Image = {
      val step = Angle.one / samples.toDouble
      def loop(count: Int): Image = {
        count match {
          case 0 => marker.at(f(Angle.zero).toVec)
          case n =>
            val angle = step * n.toDouble
            marker.at(f(angle).toVec) on loop(n - 1)
        }
      }

      loop(samples)
    }
    val spacer = Image.square(40).noFill.noStroke

    val image = (sample(parametricCircle _, 4))
      .beside(spacer)
      .beside(sample(parametricCircle _, 8))
      .beside(spacer)
      .beside(sample(parametricCircle _, 16))
  }
}
