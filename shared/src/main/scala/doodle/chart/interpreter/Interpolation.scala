package doodle
package chart
package interpreter

object Interpolation {
  def linear(inputMin: Double, inputRange: Double,
             outputMin: Double, outputRange: Double): Double => Double =
    (x: Double) => {
      val normalized = (x - inputMin) / inputRange
      (outputRange * normalized) + outputMin
    }
}
