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

package doodle.canvas.algebra

import cats.Apply
import doodle.algebra.generic.Fill
import doodle.algebra.generic.Fill.ColorFill
import doodle.algebra.generic.Fill.GradientFill
import doodle.algebra.generic.Stroke
import doodle.algebra.generic.StrokeStyle
import doodle.core.BoundingBox
import doodle.core.Cap
import doodle.core.ClosedPath
import doodle.core.Color
import doodle.core.Gradient
import doodle.core.Join
import doodle.core.OpenPath
import doodle.core.PathElement.BezierCurveTo
import doodle.core.PathElement.LineTo
import doodle.core.PathElement.MoveTo
import doodle.core.Transform
import doodle.core.font.Font
import doodle.core.font.FontFamily
import doodle.core.font.FontSize
import doodle.core.font.FontStyle
import doodle.core.font.FontWeight
import org.scalajs.dom.CanvasGradient
import org.scalajs.dom.CanvasRenderingContext2D

import scala.scalajs.js
import scala.scalajs.js.JSConverters.*

/** A canvas `Drawing` is a function that, when applied, produces a value of
  * type `A` and has the side-effect of drawing on the canvas.
  */
opaque type CanvasDrawing[A] = Function[CanvasRenderingContext2D, A]
object CanvasDrawing {
  given Apply[CanvasDrawing] with {
    def ap[A, B](ff: CanvasDrawing[A => B])(
        fa: CanvasDrawing[A]
    ): CanvasDrawing[B] =
      CanvasDrawing(ctx => ff(ctx)(fa(ctx)))

    def map[A, B](fa: CanvasDrawing[A])(f: A => B): CanvasDrawing[B] =
      CanvasDrawing(ctx => f(fa(ctx)))
  }

  def pure[A](value: A): CanvasDrawing[A] =
    CanvasDrawing(_ => value)

  /** CanvasDrawing that does nothing */
  val unit: CanvasDrawing[Unit] =
    pure(())

  extension [A](drawing: CanvasDrawing[A]) {
    def apply(ctx: CanvasRenderingContext2D): A =
      drawing(ctx)

    def >>[B](that: CanvasDrawing[B]): CanvasDrawing[B] =
      CanvasDrawing { ctx =>
        drawing(ctx)
        that(ctx)
      }

    def tap[B](that: CanvasDrawing[B]): CanvasDrawing[A] =
      CanvasDrawing { ctx =>
        val a = drawing(ctx)
        that(ctx)
        a
      }
  }

  def apply[A](f: CanvasRenderingContext2D => A): CanvasDrawing[A] = f

  def colorToCSS(color: Color): String =
    s"rgb(${color.red.get} ${color.green.get} ${color.blue.get} / ${color.alpha.get})"

  def closedPath(path: ClosedPath): CanvasDrawing[Unit] =
    CanvasDrawing { ctx =>
      ctx.beginPath()
      path.elements.foreach(elt =>
        elt match {
          case MoveTo(to)                  => ctx.moveTo(to.x, to.y)
          case LineTo(to)                  => ctx.lineTo(to.x, to.y)
          case BezierCurveTo(cp1, cp2, to) =>
            ctx.bezierCurveTo(cp1.x, cp1.y, cp2.x, cp2.y, to.x, to.y)
        }
      )
      ctx.closePath()
    }

  def fillText(
      text: String,
      x: Double,
      y: Double,
      fill: Option[Fill]
  ): CanvasDrawing[Unit] =
    CanvasDrawing.withFill(fill) {
      CanvasDrawing(canvas => canvas.fillText(text, x, y))
    }

  def measureText(text: String): CanvasDrawing[TextMetrics] =
    CanvasDrawing(canvas => canvas.measureText(text).asInstanceOf[TextMetrics])

  def openPath(path: OpenPath): CanvasDrawing[Unit] =
    CanvasDrawing { ctx =>
      ctx.beginPath()
      path.elements.foreach(elt =>
        elt match {
          case MoveTo(to)                  => ctx.moveTo(to.x, to.y)
          case LineTo(to)                  => ctx.lineTo(to.x, to.y)
          case BezierCurveTo(cp1, cp2, to) =>
            ctx.bezierCurveTo(cp1.x, cp1.y, cp2.x, cp2.y, to.x, to.y)
        }
      )
    }

  def rectangle(width: Double, height: Double): CanvasDrawing[Unit] = {
    val w = width / 2.0
    val h = height / 2.0
    CanvasDrawing { ctx =>
      ctx.beginPath()
      ctx.rect(-w, -h, width, height)
    }
  }

  def createGradient(
      ctx: CanvasRenderingContext2D,
      gradient: Gradient
  ): CanvasGradient = {
    gradient match {
      case linear: Gradient.Linear =>
        val jsGradient = ctx.createLinearGradient(
          linear.start.x,
          linear.start.y,
          linear.end.x,
          linear.end.y
        )

        linear.stops.foreach { case (color, offset) =>
          jsGradient.addColorStop(offset, colorToCSS(color))
        }

        jsGradient

      case radial: Gradient.Radial =>
        val jsGradient = ctx.createRadialGradient(
          radial.inner.x,
          radial.inner.y,
          0, // Inner radius should be 0
          radial.outer.x,
          radial.outer.y,
          radial.radius
        )

        radial.stops.foreach { case (color, offset) =>
          jsGradient.addColorStop(offset, colorToCSS(color))
        }

        jsGradient
    }
  }

  def setFill(fill: Option[Fill]): CanvasDrawing[Unit] =
    fill.map(setFill).getOrElse(unit)

  def setFill(fill: Fill): CanvasDrawing[Unit] = {
    CanvasDrawing { ctx =>
      fill match {
        case ColorFill(color)       => ctx.fillStyle = colorToCSS(color)
        case GradientFill(gradient) =>
          ctx.fillStyle = createGradient(ctx, gradient)
      }
    }
  }

  def setFont(font: Font): CanvasDrawing[Unit] =
    CanvasDrawing { ctx =>
      val fontStyle =
        font.style match {
          case FontStyle.Italic => "italic"
          case FontStyle.Normal => ""
        }

      val fontWeight =
        font.weight match {
          case FontWeight.Normal => "normal"
          case FontWeight.Bold   => "bold"
        }

      val fontSize =
        font.size match {
          case FontSize.Points(pts) => s"${pts}pt"
        }

      val fontFamily = font.family match {
        case FontFamily.Serif      => "serif"
        case FontFamily.SansSerif  => "sans-serif"
        case FontFamily.Monospaced => "monospace"
        case FontFamily.Named(get) => get
      }

      ctx.font = s"$fontStyle $fontWeight $fontSize $fontFamily"
    }

  def setStroke(stroke: Option[Stroke]): CanvasDrawing[Unit] =
    stroke.map(setStroke).getOrElse(unit)

  def setStroke(stroke: Stroke): CanvasDrawing[Unit] =
    CanvasDrawing { ctx =>
      ctx.lineWidth = stroke.width
      ctx.lineCap = stroke.cap match {
        case Cap.Butt   => "butt"
        case Cap.Round  => "round"
        case Cap.Square => "square"
      }
      ctx.lineJoin = stroke.join match {
        case Join.Bevel => "bevel"
        case Join.Round => "round"
        case Join.Miter => "miter"
      }

      stroke.style match {
        case StrokeStyle.ColorStroke(color) =>
          ctx.strokeStyle = colorToCSS(color)

        case StrokeStyle.GradientStroke(gradient) =>
          ctx.strokeStyle = createGradient(ctx, gradient)
      }

      ctx.setLineDash(
        stroke.dash match {
          case None       => js.Array()
          case Some(elts) => elts.map(_.toDouble).toJSArray
        }
      )
    }

  def setTransform(transform: Transform): CanvasDrawing[Unit] = {
    CanvasDrawing { ctx =>
      val elts = transform.elements
      ctx.setTransform(elts(0), elts(3), elts(1), elts(4), elts(2), elts(5))
    }
  }

  def textMetricsToBoundingBox(metrics: TextMetrics): BoundingBox = {
    val width = Math.abs(metrics.actualBoundingBoxLeft) + Math.abs(
      metrics.actualBoundingBoxRight
    )
    val height = Math.abs(metrics.actualBoundingBoxAscent) + Math.abs(
      metrics.actualBoundingBoxDescent
    )

    BoundingBox.centered(width, height)
  }

  def strokeText(
      text: String,
      x: Double,
      y: Double,
      stroke: Option[Stroke]
  ): CanvasDrawing[Unit] =
    CanvasDrawing.withStroke(stroke) {
      CanvasDrawing(canvas => canvas.strokeText(text, x, y))
    }

  def withFill[A](
      fill: Option[Fill]
  )(drawing: CanvasDrawing[A]): CanvasDrawing[A] =
    CanvasDrawing.setFill(fill) >> drawing.tap(
      fill
        .map(s => CanvasDrawing(ctx => ctx.fill()))
        .getOrElse(CanvasDrawing.unit)
    )

  def withStroke[A](
      stroke: Option[Stroke]
  )(drawing: CanvasDrawing[A]): CanvasDrawing[A] =
    CanvasDrawing.setStroke(stroke) >> drawing.tap(
      stroke
        .map(s => CanvasDrawing(ctx => ctx.stroke()))
        .getOrElse(CanvasDrawing.unit)
    )
}
