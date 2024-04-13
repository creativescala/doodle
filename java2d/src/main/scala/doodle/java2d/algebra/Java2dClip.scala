package doodle
package java2d
package algebra

import doodle.algebra._
import doodle.core.ClosedPath
import doodle.algebra.Path
import java.awt.{Graphics2D => gc}
import java.awt.image.BufferedImage
import java.awt.geom.Path2D
import cats.effect.unsafe.implicits.global
import doodle.core._
import doodle.java2d._
import doodle.syntax.all._
import PathElement._


trait Java2dClip extends ClipIt {
  self: doodle.algebra.Algebra { type Drawing[A] = doodle.java2d.Drawing[A] } =>
  def clipit[A](image: Drawing[A], clip_path: ClosedPath): Drawing[A] = {
    //gc.setClip(clip_path.elements)
    image
  }
}




    //val obj = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
    //val g = obj.createGraphics();
    //println(Java2D.toPath2D(clip_path.elements))
