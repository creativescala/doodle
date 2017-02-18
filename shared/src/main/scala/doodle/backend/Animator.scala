package doodle
package backend

/*
import doodle.core.{Animation, Color, DrawingContext, Point}
/**
  * An Animator gives meaning to an animation
  */
object Animator {
  def animate(animation: Animation)(implicit interpreter: Interpreter, canvas: Canvas): Unit = {
    var currentFrame = animation

    canvas.setSize(600, 600)

    def callback(): Unit = {
      canvas.clear(Color.white)
      interpreter.draw(currentFrame.image, canvas, DrawingContext.blackLines, Point.zero)
      currentFrame = currentFrame.next
    }

    canvas.setAnimationFrameCallback(callback)
  }
}
 */
