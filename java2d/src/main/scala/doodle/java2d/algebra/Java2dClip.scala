package doodle
package java2d
package algebra

import doodle.algebra._
import doodle.core.ClosedPath
import java.awt.image.BufferedImage
import cats.effect.unsafe.implicits.global
import doodle.core._
import doodle.java2d._
import PathElement._

trait Java2dClip extends ClipIt {
  self: doodle.algebra.Algebra { type Drawing[A] = doodle.java2d.Drawing[A] } =>
  def clipit[A](image: Drawing[A], clip_path: ClosedPath): Drawing[A] = {
    val obj = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
    val gg = obj.createGraphics();
    val clip_area = Java2D.toPath2D(clip_path.elements)
    gg.setClip(clip_area)
    //gg.clip(image) //Graphics2D.clip() requires java.awt.Shape as argument 
                     //whereas image is of type Drawing[A]
                     //once done, we can return the clipped
    image
  }
}
