package doodle

import doodle.core.{DrawingContext,Image}
import doodle.core.font.Font

package object backend {
  type Interpreter = Image => Finalised
  type Metrics = (Font, String) => BoundingBox
  type Configuration = (DrawingContext, Metrics)
  type Drawable = (Configuration => Interpreter, Image)
}
