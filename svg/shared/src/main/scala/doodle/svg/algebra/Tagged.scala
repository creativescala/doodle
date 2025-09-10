package doodle.svg.algebra

import doodle.algebra.Algebra

/** This algebra allows you to include any ScalaTags tag within an SVG drawing.
  */
trait Tagged[Tag] extends Algebra {

  /** Wrap the given Tag around the given Drawing. */
  def tagged[A](drawing: Drawing[A], tag: Tag): Drawing[A]

  /** A utility to include wrap a link (an a tag) around a Drawing. */
  def link[A](drawing: Drawing[A], href: String): Drawing[A]
}
