package doodle
package chart

import cats.data.Reader
import doodle.core.Image

package object interpreter {
  type Interpreter[D <: DataType] = Chart[D] => Reader[Style,Image]
}
