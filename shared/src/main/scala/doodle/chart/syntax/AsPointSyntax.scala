package doodle
package chart
package syntax

import doodle.core.Point

trait AsPointSyntax {
  implicit class AsPointOps[A](a: A) {
    def asPoint(implicit p: AsPoint[A]): Point =
      p.asPoint(a)
  }
}
