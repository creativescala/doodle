package doodle

import doodle.core.Image
import doodle.core.font.Font

package object backend {
  type Interpreter = Image => Renderable
  type Metrics = (Font, String) => BoundingBox
}
