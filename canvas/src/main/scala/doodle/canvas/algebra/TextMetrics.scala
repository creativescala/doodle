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

package doodle.canvas.algebra

import scala.scalajs.js
import scala.scalajs.js.annotation.*

// Copied from the unreleased code at
// https://github.com/scala-js/scala-js-dom/blob/main/dom/src/main/scala/org/scalajs/dom/TextMetrics.scala
//
// All documentation for facades is thanks to Mozilla Contributors at https://developer.mozilla.org/en-US/docs/Web/API
// and available under the Creative Commons Attribution-ShareAlike v2.5 or later.
// http://creativecommons.org/licenses/by-sa/2.5/
//
// Everything else is under the MIT License http://opensource.org/licenses/MIT
//

@js.native
@JSGlobal
class TextMetrics extends js.Object {

  /** Is a double giving the calculated width of a segment of inline text in
    * CSS pixels. It takes into account the current font of the context.
    */
  var width: Double = js.native

  /** Distance parallel to the baseline from the alignment point given by the
    * CanvasRenderingContext2D.textAlign property to the left side of the
    * bounding rectangle of the given text, in CSS pixels; positive numbers
    * indicating a distance going left from the given alignment point.
    */
  val actualBoundingBoxLeft: Double = js.native

  /** Returns the distance from the alignment point given by the
    * CanvasRenderingContext2D.textAlign property to the right side of the
    * bounding rectangle of the given text, in CSS pixels. The distance is
    * measured parallel to the baseline.
    */
  val actualBoundingBoxRight: Double = js.native

  /** Returns the distance from the horizontal line indicated by the
    * CanvasRenderingContext2D.textBaseline attribute to the top of the highest
    * bounding rectangle of all the fonts used to render the text, in CSS
    * pixels.
    */
  val fontBoundingBoxAscent: Double = js.native

  /** Returns the distance from the horizontal line indicated by the
    * CanvasRenderingContext2D.textBaseline attribute to the bottom of the
    * bounding rectangle of all the fonts used to render the text, in CSS
    * pixels.
    */
  val fontBoundingBoxDescent: Double = js.native

  /** Returns the distance from the horizontal line indicated by the
    * CanvasRenderingContext2D.textBaseline attribute to the top of the bounding
    * rectangle used to render the text, in CSS pixels.
    */
  val actualBoundingBoxAscent: Double = js.native

  /** Returns the distance from the horizontal line indicated by the
    * CanvasRenderingContext2D.textBaseline attribute to the bottom of the
    * bounding rectangle used to render the text, in CSS pixels.
    */
  val actualBoundingBoxDescent: Double = js.native

  /** Returns the distance from the horizontal line indicated by the
    * CanvasRenderingContext2D.textBaseline property to the top of the em square
    * in the line box, in CSS pixels.
    */
  val emHeightAscent: Double = js.native

  /** Returns the distance from the horizontal line indicated by the
    * CanvasRenderingContext2D.textBaseline property to the bottom of the em
    * square in the line box, in CSS pixels.
    */
  val emHeightDescent: Double = js.native

  /** Returns the distance from the horizontal line indicated by the
    * CanvasRenderingContext2D.textBaseline property to the hanging baseline of
    * the line box, in CSS pixels.
    */
  val hangingBaseline: Double = js.native

  /** Returns the distance from the horizontal line indicated by the
    * CanvasRenderingContext2D.textBaseline property to the alphabetic baseline
    * of the line box, in CSS pixels.
    */
  val alphabeticBaseline: Double = js.native

  /** Returns the distance from the horizontal line indicated by the
    * CanvasRenderingContext2D.textBaseline property to the ideographic baseline
    * of the line box, in CSS pixels.
    */
  val ideographicBaseline: Double = js.native

}
