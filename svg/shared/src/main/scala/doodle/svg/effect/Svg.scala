package doodle
package svg
package effect

import cats.effect.IO
import doodle.core._
import doodle.algebra.{Algebra, Picture}
import doodle.algebra.generic.{BoundingBox, Fill, Stroke}
import scalatags.generic.{Bundle, TypedTag}

final case class Svg[Builder, Output <: FragT, FragT](
    bundle: Bundle[Builder, Output, FragT] {
      type Tag <: TypedTag[Builder, Output, FragT]
    }) {
  import bundle.{svgTags => svg}
  import bundle.svgAttrs
  import bundle.implicits._

  type SvgResult[A] = (bundle.Tag, A)
  type Drawing[A] = doodle.algebra.generic.Finalized[SvgResult, A]

  implicit val context = SvgGraphicsContext(bundle)

  def render[Alg[x[_]] <: Algebra[x], A](
      frame: Frame,
      algebra: Alg[Drawing],
      picture: Picture[Alg, Drawing, A]): IO[(Output, A)] = {
    renderWithoutRootTag(algebra, picture)
      .map { case (bb, tags, a) => (svgTag(bb, frame)(tags).render, a) }
  }

  /** Render to SVG without wrapping with a root <svg> tag. */
  def renderWithoutRootTag[Alg[x[_]] <: Algebra[x], A](
      algebra: Alg[Drawing],
      picture: Picture[Alg, Drawing, A]): IO[(BoundingBox, bundle.Tag, A)] = {
    for {
      drawing <- IO { picture(algebra) }
      (bb, rdr) = drawing.runA(List.empty).value
      (_, (tags, a)) = rdr.run(Transform.verticalReflection).value
    } yield (bb, tags, a)
  }

  /** Given a bounding box and a size specification create a <svg> tag that has the
    * correct size and viewbox */
  def svgTag(bb: BoundingBox, frame: Frame): bundle.Tag =
    frame.size match {
      case Size.FitToPicture(border) =>
        val w = bb.width + (2 * border)
        val h = bb.height + (2 * border)
        svg.svg(
          svgAttrs.width := w,
          svgAttrs.height := h,
          svgAttrs.viewBox := s"${bb.left - border} ${bb.bottom - border} ${w} ${h}",
          bundle.attrs.style :=
            frame.background.map(c => s"background-color: ${Svg.toHSLA(c)};").getOrElse(""))

      case Size.FixedSize(w, h) =>
        svg.svg(svgAttrs.width := w,
                svgAttrs.height := h,
                svgAttrs.viewBox := s"${-w / 2} ${-h / 2} ${w} ${h}",
                bundle.attrs.style :=
                  frame.background.map(c => s"background-color: ${Svg.toHSLA(c)};").getOrElse(""))

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
}
object Svg {
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
    builder ++= s"stroke: ${toHSLA(stroke.color)};"
    builder ++= s"stroke-linecap: ${linecap}; "
    builder ++= s"stroke-linejoin: ${linejoin}; "
    builder ++= (stroke.dash match {
      case Some(d) => d.map(a => f"stroke-dasharray: $a%.2f; ").mkString(" ")
      case None => ""
    })

    builder.toString
  }

  def toStyle(fill: Fill): String = {
    s"fill: ${toHSLA(fill.color)};"
  }

  def toStyle(stroke: Option[Stroke], fill: Option[Fill]): String = {
    stroke.fold("stroke: none;")(this.toStyle(_)) ++ " " ++ fill.fold("fill: none;")(this.toStyle(_))
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

  private def format(d: Double): String = d.toString.replaceFirst("\\.0+$","")

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
        builder ++= s"C ${format(cp1.x)},${format(cp1.y)} ${format(cp2.x)},${format(cp2.y)} ${format(end.x)},${format(end.y)} "
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

  def toHSLA(color: Color): String = {
    val (h, s, l, a) =
      (color.hue, color.saturation, color.lightness, color.alpha)
    s"hsla(${h.toDegrees}, ${s.get * 100}%, ${l.get * 100}%, ${a.get})"
  }

  def toRGB(color: Color): String = {
    val (r, g, b) = (color.red, color.green, color.blue)
    s"rgb(${r.get}, ${g.get}, ${b.get})"
  }
}
