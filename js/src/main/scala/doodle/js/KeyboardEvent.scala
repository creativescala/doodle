package doodle
package js

import org.scalajs.dom
import doodle.backend.Key

object KeyboardEvent {
  import doodle.backend.Key._

  def toKey(evt: dom.raw.KeyboardEvent): Key =
    evt.key match {
      case "Control" => Control
      case "Alt" => Alt
      case "Shift" => Shift

      case "ArrowUp" => Up
      case "ArrowDown" => Down
      case "ArrowLeft" => Left
      case "ArrowRight" => Right

      case "a" => Character('a')
      case "b" => Character('b')
      case "c" => Character('c')
      case "d" => Character('d')
      case "e" => Character('e')
      case "f" => Character('f')
      case "g" => Character('g')
      case "h" => Character('h')
      case "i" => Character('i')
      case "j" => Character('j')
      case "k" => Character('k')
      case "l" => Character('l')
      case "m" => Character('m')
      case "n" => Character('n')
      case "o" => Character('o')
      case "p" => Character('p')
      case "q" => Character('q')
      case "r" => Character('r')
      case "s" => Character('s')
      case "t" => Character('t')
      case "u" => Character('u')
      case "v" => Character('v')
      case "w" => Character('w')
      case "x" => Character('x')
      case "y" => Character('y')
      case "z" => Character('z')

      case _ => Unknown
    }
}
