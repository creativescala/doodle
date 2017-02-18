package doodle

package object core {
  private def reduceImages(images: Seq[Image])(combine: (Image, Image) => Image): Image =
    images match {
      case Seq()      => Image.Empty
      case Seq(image) => image
      case images     => images.reduceLeft(combine)
    }

  def allBeside(images: Seq[Image]): Image =
    reduceImages(images)(_ beside _)

  def allAbove(images: Seq[Image]): Image =
    reduceImages(images)(_ above _)

  def allBelow(images: Seq[Image]): Image =
    reduceImages(images)(_ below _)

  def allOn(images: Seq[Image]): Image =
    reduceImages(images)(_ on _)

  def allUnder(images: Seq[Image]): Image =
    reduceImages(images)(_ under _)
}
