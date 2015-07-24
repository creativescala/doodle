package doodle
package backend

import doodle.core.{Animation, Color, DrawingContext, Vec}

/**
  * An Animator gives meaning to an animation
  */
object Animator {
  def animate(animation: Animation)(implicit interpreter: Interpreter, canvas: Canvas): Unit = {
    var currentFrame = animation

    canvas.setSize(600, 600)

    def callback(): Unit = {
      canvas.setFill(Color.white)
      canvas.clear()
      interpreter.draw(currentFrame.image, canvas, DrawingContext.blackLines, Vec.zero)
      currentFrame = currentFrame.next
    }

    canvas.setAnimationFrameCallback(callback)
  }
}
