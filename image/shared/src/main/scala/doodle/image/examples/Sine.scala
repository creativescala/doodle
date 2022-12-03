/*
 * Copyright 2015 Creative Scala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package doodle
package image
package examples

import cats.implicits._
import doodle.core._
import doodle.image.Image
import doodle.image.Image._
import doodle.image.syntax.all._
import doodle.image.syntax.core._
import doodle.random._

object Sine {
  import Parametric.AngularCurve

  val underBlue = Color.rgb(39.uByte, 170.uByte, 255.uByte)

  def sine(width: Int, amplitude: Double, period: Int): AngularCurve = {
    val frequency = width.toDouble / period.toDouble
    AngularCurve {
      Parametric.sine(amplitude, frequency) andThen (pt =>
        Point((pt.x - 0.5) * width, pt.y)
      )
    }
  }

  def noisySine(
      curve: AngularCurve,
      stdDev: Double = 15.0
  ): Angle => Random[Point] =
    (angle: Angle) =>
      Random.normal(0.0, stdDev).map { offset =>
        curve(angle) + Vec(0, offset)
      }

  val randomAngle: Random[Angle] = Random.double.map(a => a.turns)

  def noiseSamples(n: Int, f: Angle => Random[Point]): Random[List[Point]] =
    (0 until n).toList.foldM(List.empty[Point]) { (accum, _) =>
      randomAngle.flatMap(f).map(pt => pt :: accum)
    }

  val samples: Random[List[Point]] =
    noiseSamples(100, noisySine(sine(2000, 150, 400)))

  def error(curve: AngularCurve, data: List[Point]): Double =
    data.foldLeft(0.0) { (error, pt) =>
      val diff = curve(((pt.x + 1000.0) / 2000.0).turns).y - pt.y
      error + (diff * diff)
    }

  def errorPlot(data: List[Point], periods: List[Int]): Image = {
    val errors = periods.sorted.map(p =>
      Point(p.toDouble, error(sine(2000, 150, p), data))
    )
    val maxPeriod = periods.max
    val max = errors.map(pt => pt.y).max
    val scaled =
      errors.map(pt => Point(pt.x, (pt.y * maxPeriod * (9.0 / 16.0)) / max))

    val points =
      (scaled
        .map(pt =>
          Image.circle(13.0).at(pt.toVec).fillColor(Color.white).noStroke
        ))
        .allOn
    val curve =
      interpolatingSpline(scaled).strokeWidth(11.0).strokeColor(Color.white)

    points on curve
  }

  def gradientDescent(
      data: List[Point],
      periods: List[Int],
      start: Int,
      goal: Int,
      step: Int
  ): Image = {
    val steps = List.range(start, goal, step)
    val errors = periods.sorted.map(p =>
      Point(p.toDouble, error(sine(2000, 150, p), data))
    )
    val maxPeriod = periods.max
    val max = errors.map(pt => pt.y).max
    val scaled =
      errors.map(pt => Point(pt.x, (pt.y * maxPeriod * (9.0 / 16.0)) / max))

    // val points =
    // (scaled.map(pt => Image.circle(13.0).at(pt.toVec).fillColor(Color.white).noStroke)).allOn
    val curve =
      interpolatingSpline(scaled).strokeWidth(11.0).strokeColor(Color.white)

    val (_, visited) =
      steps
        .map(p => Point(p.toDouble, error(sine(2000, 150, p), data)))
        .map(pt => Point(pt.x, (pt.y * maxPeriod * (9.0 / 16.0)) / max))
        .map(pt => Image.circle(13.0).at(pt.toVec).noStroke)
        .foldLeft((1.0, List.empty[Image])) { (accum, img) =>
          val (lightness, soFar) = accum
          (
            lightness * 0.9,
            img.fillColor(Color.red.lightness(lightness.normalized)) :: soFar
          )
        }

    (visited.allOn) on curve
  }

  def errorBars(data: List[Point], curve: AngularCurve, width: Int): Image = {
    import PathElement._

    (
      data.map { pt =>
        val angle = ((pt.x + (width / 2)) / width).turns
        val predicted = curve(angle)

        Image
          .openPath(List(moveTo(pt.x, pt.y), lineTo(predicted.x, predicted.y)))
          .strokeColor(underBlue)
          .strokeWidth(7.0)
      }
    ).allOn
  }

  def styledSine(curve: AngularCurve): Image =
    interpolatingSpline(curve.sample(200))
      .strokeWidth(11.0)
      .strokeColor(Color.white)

  val spacer = Image.rectangle(2000, 500).noStroke.noFill

  val sines = {
    val s1 =
      interpolatingSpline(sine(2000, 150, 200).sample(200)).strokeWidth(11.0)
    val s2 =
      interpolatingSpline(sine(2000, 150, 400).sample(200)).strokeWidth(11.0)
    val s3 =
      interpolatingSpline(sine(2000, 150, 800).sample(200)).strokeWidth(11.0)

    val c1 = underBlue.lightness(0.8.normalized)
    val c3 = c1.spin(180.degrees)

    s2.strokeColor(Color.white) on s1.strokeColor(c1) on s3.strokeColor(c3)
  }

  val images =
    samples.map { data =>
      val periods = List.range(100, 1625, 25)
      val dataPlot =
        (data
          .map(pt => Image.circle(9).at(pt.toVec)))
          .allOn
          .fillColor(underBlue)
          .noStroke
      val sines = List(100, 200, 400, 800, 1600).map { p =>
        styledSine(sine(2000, 150, p)) on spacer
      }
      val bars = errorBars(data, sine(2000, 150, 1600), 2000)
      val squaredError = errorPlot(data, periods)
      val descent = gradientDescent(data, periods, 350, 410, 10)

      descent :: squaredError :: (dataPlot on spacer) :: (sines.last on dataPlot on bars) :: (sines map (
        s => dataPlot on s
      ))
    }.run

  /*
  def saveAll: Unit = {
    val descent :: error :: data :: bars :: rest = images
    descent.save[Pdf]("sine-descent.pdf")
    error.save[Pdf]("sine-error.pdf")
    data.save[Pdf]("sine-data.pdf")
    bars.save[Pdf]("sine-error-bars.pdf")
    rest.zipWithIndex.map{ case (s, i) => s.save[Pdf](s"sine-$i.pdf") }
  }
   */
}
