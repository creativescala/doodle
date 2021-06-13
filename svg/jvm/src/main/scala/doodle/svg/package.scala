package doodle

package object svg {
  val jvm = new doodle.svg.algebra.JvmAlgebraModule {}

  // Need to re-export most of the things from JsAlgebraModule because directly
  // extending JsAlgebraModule from the package object leads to a compilation
  // error
  type Algebra[F[_]] = jvm.Algebra[F]
  val algebraInstance = jvm.algebraInstance
  type Drawing[A] = jvm.Drawing[A]
  val Svg = jvm.Svg
  type Tag = jvm.Tag
  type Frame = doodle.svg.effect.Frame
  val Frame = doodle.svg.effect.Frame
  implicit val svgWriter = doodle.svg.effect.SvgWriter

  type Picture[A] = doodle.algebra.Picture[Algebra, Drawing, A]
  object Picture {
    def apply(f: Algebra[Drawing] => Drawing[Unit]): Picture[Unit] = {
      new Picture[Unit] {
        def apply(implicit algebra: Algebra[Drawing]): Drawing[Unit] =
          f(algebra)
      }
    }
  }
}
