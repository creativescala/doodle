package doodle
package java2d
package algebra

import doodle.algebra._
import doodle.core.ClosedPath
import doodle.algebra.Path
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import cats.effect.unsafe.implicits.global
import doodle.core._
import doodle.java2d._
import doodle.syntax.all._


trait Java2dClip extends ClipIt {
  self: doodle.algebra.Algebra { type Drawing[A] = doodle.java2d.Drawing[A] } =>
  def clipit[A](image: Drawing[A], clip_path: ClosedPath): Drawing[A] = {
    val obj = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
    val g = obj.createGraphics();
    g.setClip(clip_path)
    g.clip(image)
    //g.drawImage(image, 0, 0, null);
    image
  }



}
