package doodle

import doodle.core.{DrawingContext,Image}
import doodle.core.font.Font

package object backend {
  import cats.data.Reader

  type Metrics = (Font, String) => BoundingBox
  type Configuration = (DrawingContext, Metrics)
  type Finaliser = Reader[Configuration, Image => Finalised]
  type Renderer = Reader[Canvas, Finalised => Unit]
}
