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

package doodle
package core

import doodle.syntax.all.*
import munit.FunSuite

class ColorSuite extends FunSuite {
  test("toRGBA should convert to expected RGBA color") {
    val blueHSLA = Color.hsl(240.degrees, 0.5, 0.5).toRGBA
    val blueRGBA = Color.rgb(64.uByte, 64.uByte, 191.uByte)
    assert(blueHSLA ~= blueRGBA)

    val greenHSLA = Color.hsl(120.degrees, 0.5, 0.5).toRGBA
    val greenRGBA = Color.rgb(64.uByte, 191.uByte, 64.uByte)
    assert(greenHSLA ~= greenRGBA)

    val redHSLA = Color.hsl(0.degrees, 0.75, 0.5).toRGBA
    val redRGBA = Color.rgb(223.uByte, 32.uByte, 32.uByte)
    assert(redHSLA ~= redRGBA)
  }

  test("toHSLA should converts to expected HSLA color") {
    val blueHSLA = Color.hsl(240.degrees, 0.5, 0.5)
    val blueRGBA = Color.rgb(64.uByte, 64.uByte, 191.uByte).toHSLA
    assert(blueHSLA ~= blueRGBA)

    val greenHSLA = Color.hsl(120.degrees, 0.5, 0.5)
    val greenRGBA = Color.rgb(64.uByte, 191.uByte, 64.uByte).toHSLA
    assert(greenHSLA ~= greenRGBA)

    val redHSLA = Color.hsl(0.degrees, 0.75, 0.5)
    val redRGBA = Color.rgb(223.uByte, 32.uByte, 32.uByte).toHSLA
    assert(redHSLA ~= redRGBA)
  }

  test("HSLA with 0 saturation should convert to gray RGBA") {
    val grey1HSLA = Color.hsl(0.degrees, 0, 0.5).toRGBA
    val grey1RGBA = Color.rgb(128.uByte, 128.uByte, 128.uByte)
    assert(grey1HSLA ~= grey1RGBA)

    val grey2HSLA = Color.hsl(0.degrees, 0, 1.0).toRGBA
    val grey2RGBA = Color.rgb(255.uByte, 255.uByte, 255.uByte)
    assert(grey2HSLA ~= grey2RGBA)
  }

  test("HSLA spin should transform correctly") {
    val original = Color.hsl(120.degrees, 0.5, 0.5)
    val spun = original.spin(60.degrees)
    val unspun = original.spin(-60.degrees)

    assert(spun ~= Color.hsl(180.degrees, 0.5, 0.5))
    assert(unspun ~= Color.hsl(60.degrees, 0.5, 0.5))
  }

  test("Fade in/out should transform correctly") {
    val original = Color.hsla(120.degrees, 0.5, 0.5, 0.5)
    val fadeOut = original.fadeOut(0.5.normalized)
    val fadeIn = original.fadeIn(0.5.normalized)

    assert(fadeOut.alpha == (0.0.normalized))
    assert(fadeIn.alpha == (1.0.normalized))
  }

  test("parse hex colors correctly") {
    // With leading #
    assertEquals(Color.fromHex("#f00"), Color.red)
    assertEquals(Color.fromHex("#0f0"), Color.lime)
    assertEquals(Color.fromHex("#00f"), Color.blue)

    assert(
      Color.fromHex("#f009") ~= Color.red.alpha((153.0 / 255.0).normalized)
    )
    assert(
      Color.fromHex("#0f09") ~= Color.lime.alpha((153.0 / 255.0).normalized)
    )
    assert(
      Color.fromHex("#00f9") ~= Color.blue.alpha((153.0 / 255.0).normalized)
    )

    assertEquals(Color.fromHex("#ff0000"), Color.red)
    assertEquals(Color.fromHex("#00ff00"), Color.lime)
    assertEquals(Color.fromHex("#0000ff"), Color.blue)

    assert(
      Color.fromHex("#ff000099") ~= Color.red.alpha((153.0 / 255.0).normalized)
    )
    assert(
      Color.fromHex("#00ff0099") ~= Color.lime.alpha((153.0 / 255.0).normalized)
    )
    assert(
      Color.fromHex("#0000ff99") ~= Color.blue.alpha((153.0 / 255.0).normalized)
    )

    // Without leading #
    assertEquals(Color.fromHex("f00"), Color.red)
    assertEquals(Color.fromHex("0f0"), Color.lime)
    assertEquals(Color.fromHex("00f"), Color.blue)

    assert(Color.fromHex("f009") ~= Color.red.alpha((153.0 / 255.0).normalized))
    assert(
      Color.fromHex("0f09") ~= Color.lime.alpha((153.0 / 255.0).normalized)
    )
    assert(
      Color.fromHex("00f9") ~= Color.blue.alpha((153.0 / 255.0).normalized)
    )

    assertEquals(Color.fromHex("ff0000"), Color.red)
    assertEquals(Color.fromHex("00ff00"), Color.lime)
    assertEquals(Color.fromHex("0000ff"), Color.blue)

    assert(
      Color.fromHex("ff000099") ~= Color.red.alpha((153.0 / 255.0).normalized)
    )
    assert(
      Color.fromHex("00ff0099") ~= Color.lime.alpha((153.0 / 255.0).normalized)
    )
    assert(
      Color.fromHex("0000ff99") ~= Color.blue.alpha((153.0 / 255.0).normalized)
    )
  }
}
