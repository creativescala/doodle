/*
 * Copyright 2015 Creative Scala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package doodle.examples

import cats.effect.unsafe.implicits.global
import doodle.core.*
import doodle.java2d.*
import doodle.syntax.all.*

object LandmarkLayoutExamples {

  /** Example 1: Percentage-based sizing
    *
    * Demonstrates using Landmarks to create scaled versions of shapes
    */
  def percentageSizing = {
    val baseCircle = circle(50).fillColor(Color.royalBlue)

    // Double the size (200%)
    val doubleSize = baseCircle.size(Landmark.percent(200, 200))

    // Half the size (50%)
    val halfSize = baseCircle.size(Landmark.percent(50, 50))

    // Different scaling in each dimension
    val stretched = baseCircle.size(Landmark.percent(100, 150))

    halfSize.beside(baseCircle).beside(doubleSize).beside(stretched)
  }

  /** Example 2: Percentage-based margins
    *
    * Shows how to use relative margins for responsive spacing
    */
  def percentageMargins = {
    val box = square(100).fillColor(Color.crimson)

    // Add margin of 50% of current width/height on all sides
    val withMargin = box.margin(Landmark.percent(50, 50))

    // Add different margins on each side (top/bottom: 25%, left/right: 75%)
    val asymmetricMargin = box.margin(
      Landmark.percent(0, 75), // horizontal (left/right)
      Landmark.percent(25, 0) // vertical (top/bottom)
    )

    withMargin.beside(asymmetricMargin)
  }

  /** Example 3: Mixing absolute and percentage values
    *
    * Demonstrates combining Point (absolute) and Percent coordinates
    */
  def mixedValues = {
    val rect = rectangle(80, 40).fillColor(Color.seaGreen)

    // Size: 100% width, 200 pixels height
    val mixedSize = rect.size(
      Landmark(Coordinate.percent(100), Coordinate.point(200)),
      Landmark(Coordinate.percent(100), Coordinate.point(200))
    )

    rect.beside(mixedSize)
  }

  /** Example 4: Responsive grid layout
    *
    * Creates a grid where elements scale proportionally
    */
  def responsiveGrid = {
    val cell = square(60).fillColor(Color.hotpink)

    // Each cell scales to 80% of its size
    val scaled = cell.size(Landmark.percent(80, 80))

    // Add uniform margin of 20% around each cell
    val withSpacing = scaled.margin(Landmark.percent(20, 20))

    val row = withSpacing.beside(withSpacing).beside(withSpacing)
    row.above(row).above(row)
  }

  /** Example 5: Progressive scaling
    *
    * Chain percentage operations to create compound effects
    */
  def progressiveScaling = {
    val start = circle(40).fillColor(Color.orange)

    // Each subsequent circle is 125% of the previous one
    val s1 = start.size(Landmark.percent(125, 125))
    val s2 = s1.size(Landmark.percent(125, 125))
    val s3 = s2.size(Landmark.percent(125, 125))

    start.beside(s1).beside(s2).beside(s3)
  }

  def main(args: Array[String]): Unit = {
    // Render all examples
    percentageSizing.draw()
    percentageMargins.draw()
    mixedValues.draw()
    responsiveGrid.draw()
    progressiveScaling.draw()
  }
}
