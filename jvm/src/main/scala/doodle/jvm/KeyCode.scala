package doodle
package jvm

import doodle.backend.Key
import java.awt.event.KeyEvent

object KeyCode {
  import doodle.backend.Key._

  /** Convert a Java AWT key code to a Key */
  def toKey(code: Int): Key =
    code match {
      case KeyEvent.VK_CONTROL => Control
      case KeyEvent.VK_ALT => Alt
      case KeyEvent.VK_SHIFT => Shift

      case KeyEvent.VK_UP => Up
      case KeyEvent.VK_DOWN => Down
      case KeyEvent.VK_LEFT => Left
      case KeyEvent.VK_RIGHT => Right

      case KeyEvent.VK_A => Character('a')
      case KeyEvent.VK_B => Character('b')
      case KeyEvent.VK_C => Character('c')
      case KeyEvent.VK_D => Character('d')
      case KeyEvent.VK_E => Character('e')
      case KeyEvent.VK_F => Character('f')
      case KeyEvent.VK_G => Character('g')
      case KeyEvent.VK_H => Character('h')
      case KeyEvent.VK_I => Character('i')
      case KeyEvent.VK_J => Character('j')
      case KeyEvent.VK_K => Character('k')
      case KeyEvent.VK_L => Character('l')
      case KeyEvent.VK_M => Character('m')
      case KeyEvent.VK_N => Character('n')
      case KeyEvent.VK_O => Character('o')
      case KeyEvent.VK_P => Character('p')
      case KeyEvent.VK_Q => Character('q')
      case KeyEvent.VK_R => Character('r')
      case KeyEvent.VK_S => Character('s')
      case KeyEvent.VK_T => Character('t')
      case KeyEvent.VK_U => Character('u')
      case KeyEvent.VK_V => Character('v')
      case KeyEvent.VK_W => Character('w')
      case KeyEvent.VK_X => Character('x')
      case KeyEvent.VK_Y => Character('y')
      case KeyEvent.VK_Z => Character('z')

      case KeyEvent.VK_SPACE => Character(' ')
      case KeyEvent.VK_TAB => Character('\t')

        // TODO: Complete the rest of the key codes

      case _ => Unknown
    }
}
