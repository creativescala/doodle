package doodle
package svg
package algebra

import cats.Apply
import cats.effect.IO
import doodle.core._
import doodle.core.font._
import doodle.algebra.Picture
import doodle.algebra.generic.{Fill, Stroke}
import doodle.svg.effect.Size
import scala.collection.mutable

trait SvgModule { self: Base =>
  object Svg {
    implicit val svgResultApply: Apply[SvgResult] =
      new Apply[SvgResult] {
        def ap[A, B](ff: SvgResult[(A) => B])(
            fa: SvgResult[A]): SvgResult[B] = {
          val (t1, s1, fab) = ff
          val (t2, s2, a) = fa

          (svg.g(t1, t2), s1.union(s2), fab(a))
        }

        def map[A, B](fa: SvgResult[A])(f: (A) => B): SvgResult[B] = {
          val (t, s, a) = fa
          (t, s, f(a))
        }
      }

    val svg = bundle.svgTags
    val svgAttrs = bundle.svgAttrs
    val implicits = bundle.implicits
    import implicits.{Tag => _, _}

    def render[Alg[x[_]] <: doodle.algebra.Algebra[x], A](
        frame: Frame,
        algebra: Alg[Drawing],
        picture: Picture[Alg, Drawing, A]): IO[(Output, A)] = {
      renderWithoutRootTag(algebra, picture)
        .map { case (bb, tags, a) => (svgTag(bb, frame)(tags).render, a) }
    }

    /** Render to SVG without wrapping with a root <svg> tag. */
    def renderWithoutRootTag[Alg[x[_]] <: doodle.algebra.Algebra[x], A](
        algebra: Alg[Drawing],
        picture: Picture[Alg, Drawing, A]): IO[(BoundingBox, Tag, A)] = {
      for {
        drawing <- IO { picture(algebra) }
        (bb, rdr) = drawing.runA(List.empty).value
        (_, (tags, set, a)) = rdr.run(Transform.verticalReflection).value
        tagsWithGradients = svg.g(svg.defs(set.toList: _*), tags)
      } yield (bb, tagsWithGradients, a)
    }

    /** Given a bounding box and a size specification create a <svg> tag that has the
      * correct size and viewbox */
    def svgTag(bb: BoundingBox, frame: Frame): Tag =
      frame.size match {
        case Size.FitToPicture(border) =>
          val w = bb.width + (2 * border)
          val h = bb.height + (2 * border)
          svg.svg(
            svgAttrs.xmlns := s"http://www.w3.org/2000/svg",
            svgAttrs.width := w,
            svgAttrs.height := h,
            svgAttrs.viewBox := s"${bb.left - border} ${bb.bottom - border} ${w} ${h}",
            bundle.attrs.style :=
              frame.background
                .map(c => s"background-color: ${Svg.toHSLA(c)};")
                .getOrElse("")
          )

        case Size.FixedSize(w, h) =>
          svg.svg(
            svgAttrs.width := w,
            svgAttrs.height := h,
            svgAttrs.viewBox := s"${-w / 2} ${-h / 2} ${w} ${h}",
            bundle.attrs.style :=
              frame.background
                .map(c => s"background-color: ${Svg.toHSLA(c)};")
                .getOrElse("")
          )

      }

    def textTag(text: String, font: Font): Tag = {
      val fontFamily =
        font.family match {
          case FontFamily.Serif       => "serif"
          case FontFamily.SansSerif   => "sans-serif"
          case FontFamily.Monospaced  => "monospaced"
          case FontFamily.Named(name) => name
        }

      val fontStyle =
        font.style match {
          case FontStyle.Italic => "italic"
          case FontStyle.Normal => "normal"
        }

      val fontWeight =
        font.weight match {
          case FontWeight.Bold   => "bold"
          case FontWeight.Normal => "normal"
        }

      val style = s"""
        |font-family: ${fontFamily};
        |font-style: ${fontStyle};
        |font-weight: ${fontWeight};
        """.stripMargin

      svg.text(svgAttrs.style := style, text)
    }

    /**
      * Transform from client coordinates to local coordinates
      */
    def inverseClientTransform(bb: BoundingBox, size: Size): Transform = {
      size match {
        case Size.FitToPicture(border) =>
          val w = bb.width + (2 * border)
          val h = bb.height + (2 * border)
          Transform.screenToLogical(w, h)

        case Size.FixedSize(w, h) =>
          Transform.screenToLogical(w, h)
      }
    }

    /**
      * Given stroke and fill returns a `String` representing the stroke and fill rendered as SVG styles.
      *
      * If the fill specifies a gradient that gradient, represented in SVG form as
      * a Tag, is added to the given Set as a side-effect.
      */
    def toStyle(stroke: Option[Stroke],
                fill: Option[Fill],
                gradients: mutable.Set[Tag]): String = {
      val f = fill.fold("fill: none;")(f => this.toStyle(f, gradients))
      val s = stroke.fold("stroke: none;")(Svg.toStyle(_))

      s ++ " " ++ f
    }

    /**
      * Given a Fill return the string to insert as the style part of the tag being rendered
      *
      * Additionally, if this fill represents a gradient add that gradient to the
      * given Set as a side-effect. In SVG gradients cannot be specified inline.
      * Hence this construction.
      */
    def toStyle(fill: Fill, gradients: mutable.Set[Tag]): String = {
      fill match {
        case Fill.ColorFill(c) => s"fill: ${Svg.toHSLA(c)};"
        case Fill.GradientFill(g) =>
          val (id, gradient) = toSvgGradient(g)
          gradients += gradient
          s"fill: url('#${id}'); "
      }
    }

    def toSvgGradient(gradient: Gradient): (String, Tag) =
      gradient match {
        case linear: Gradient.Linear => this.toSvgLinearGradient(linear)
        case radial: Gradient.Radial => this.toSvgRadialGradient(radial)
      }

    def toSvgLinearGradient(gradient: Gradient.Linear): (String, Tag) = {
      val (x1, y1, x2, y2) =
        (gradient.start.x, gradient.start.y, gradient.end.x, gradient.end.y)
      val id = Svg.toGradientId(gradient)
      val spreadMethod = Svg.toSvgSpreadMethod(gradient.cycleMethod)
      val stops = gradient.stops.map(this.toSvgGradientStop)
      val domGradient = svg.linearGradient(
        svgAttrs.id := id,
        svgAttrs.x1 := x1,
        svgAttrs.y1 := y1,
        svgAttrs.x2 := x2,
        svgAttrs.y2 := y2,
        svgAttrs.spreadMethod := spreadMethod,
        svgAttrs.gradientUnits := "objectBoundingBox"
      )(stops: _*)

      id -> domGradient
    }

    def toSvgRadialGradient(gradient: Gradient.Radial): (String, Tag) = {
      val (cx, cy, fx, fy, r) = (gradient.outer.x,
                                 gradient.outer.y,
                                 gradient.inner.x,
                                 gradient.inner.y,
                                 gradient.radius)
      val id = Svg.toGradientId(gradient)
      val spreadMethod = Svg.toSvgSpreadMethod(gradient.cycleMethod)
      val stops = gradient.stops.map(this.toSvgGradientStop)
      val domGradient = svg.radialGradient(
        svgAttrs.id := id,
        svgAttrs.cx := cx,
        svgAttrs.cy := cy,
        svgAttrs.fx := fx,
        svgAttrs.fy := fy,
        svgAttrs.r := r,
        svgAttrs.spreadMethod := spreadMethod,
        svgAttrs.gradientUnits := "userSpaceOnUse"
      )(stops: _*)

      id -> domGradient
    }

    def toSvgGradientStop(tuple: (Color, Double)): Tag = {
      val (c, offset) = tuple
      val color = Svg.toRGB(c)
      val opacity = c.alpha.get
      svg.stop(svgAttrs.offset := offset,
               svgAttrs.stopColor := color,
               svgAttrs.stopOpacity := opacity)
    }

    def toStyle(stroke: Stroke): String = {
      val builder = new StringBuilder(64)

      val linecap = stroke.cap match {
        case Cap.Butt   => "butt"
        case Cap.Round  => "round"
        case Cap.Square => "square"
      }
      val linejoin = stroke.join match {
        case Join.Bevel => "bevel"
        case Join.Round => "round"
        case Join.Miter => "miter"
      }
      builder ++= s"stroke-width: ${stroke.width}px; "
      builder ++= s"stroke: ${toHSLA(stroke.color)}; "
      builder ++= s"stroke-linecap: ${linecap}; "
      builder ++= s"stroke-linejoin: ${linejoin}; "
      builder ++= (stroke.dash match {
        case Some(d) => d.map(a => f"stroke-dasharray: $a%.2f; ").mkString(" ")
        case None    => ""
      })

      builder.toString
    }

    def toSvgTransform(tx: Transform): String = {
      val elt = tx.elements
      val a = elt(0)
      val b = elt(3)
      val c = elt(1)
      val d = elt(4)
      val e = elt(2)
      val f = elt(5)
      s"matrix($a,$b,$c,$d,$e,$f)"
    }

    sealed trait PathType
    case object Open extends PathType
    case object Closed extends PathType

    private def format(d: Double): String =
      d.toString.replaceFirst("\\.0+$", "")

    def toSvgPath(elts: List[PathElement], pathType: PathType): String = {
      import PathElement._
      import scala.collection.mutable.StringBuilder

      val builder = new StringBuilder(64, "M 0,0 ")
      elts.foreach {
        case MoveTo(end) =>
          builder ++= s"M ${format(end.x)},${format(end.y)} "
        case LineTo(end) =>
          builder ++= s"L ${format(end.x)},${format(end.y)} "
        case BezierCurveTo(cp1, cp2, end) =>
          builder ++= s"C ${format(cp1.x)},${format(cp1.y)} ${format(cp2.x)},${format(
            cp2.y)} ${format(end.x)},${format(end.y)} "
      }
      pathType match {
        case Open   => builder.toString
        case Closed => (builder += 'Z').toString
      }
    }

    def toSvgPath(points: Array[Point], pathType: PathType): String = {
      import scala.collection.mutable.StringBuilder

      val builder = new StringBuilder(points.size * 10)
      var first = true
      points.foreach { pt =>
        if (first) {
          first = false
          builder ++= s"M ${format(pt.x)},${format(pt.y)} "
        } else builder ++= s"L ${format(pt.x)},${format(pt.y)} "
      }

      pathType match {
        case Open   => builder.toString
        case Closed => (builder += 'Z').toString
      }
    }

    def toSvgSpreadMethod(cycleMethod: Gradient.CycleMethod): String =
      cycleMethod match {
        case Gradient.CycleMethod.NoCycle => "pad"
        case Gradient.CycleMethod.Reflect => "reflect"
        case Gradient.CycleMethod.Repeat  => "repeat"
      }

    def toHSLA(color: Color): String = {
      val (h, s, l, a) =
        (color.hue, color.saturation, color.lightness, color.alpha)
      s"hsla(${h.toDegrees}, ${s.get * 100}%, ${l.get * 100}%, ${a.get})"
    }

    def toRGB(color: Color): String = {
      val (r, g, b) = (color.red, color.green, color.blue)
      s"rgb(${r.get}, ${g.get}, ${b.get})"
    }

    def toGradientId(gradient: Gradient): String =
      s"gradient-${gradient.hashCode.toString}"
  }
}
