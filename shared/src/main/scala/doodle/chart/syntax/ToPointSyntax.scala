package doodle
package chart
package syntax

import doodle.core.Point

trait ToPointSyntax {
  implicit class ToPointOps[A](a: A) {
    def toPoint(implicit p: ToPoint[A]): Point =
      p.toPoint(a)
  }
}
