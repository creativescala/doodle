package doodle

package object core {
  // HACK: Add a proper empty image!
  private def emptyImage: Image =
    Circle(0) lineWidth 0

  def allBeside(images: Seq[Image]): Image =
    images.foldLeft(emptyImage)(_ beside _)

  def allAbove(images: Seq[Image]): Image =
    images.foldLeft(emptyImage)(_ above _)

  def allBelow(images: Seq[Image]): Image =
    images.foldLeft(emptyImage)(_ below _)

  def allOn(images: Seq[Image]): Image =
    images.foldLeft(emptyImage)(_ on _)

  def allUnder(images: Seq[Image]): Image =
    images.foldLeft(emptyImage)(_ under _)
}
