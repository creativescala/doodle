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
  test("toRgb should convert to expected Rgb color") {
    val blueHsl = Color.hsl(240.degrees, 0.5, 0.5).toRgb
    val blueRgb = Color.rgb(64.uByte, 64.uByte, 191.uByte)
    assert(blueHsl ~= blueRgb)

    val greenHsl = Color.hsl(120.degrees, 0.5, 0.5).toRgb
    val greenRgb = Color.rgb(64.uByte, 191.uByte, 64.uByte)
    assert(greenHsl ~= greenRgb)

    val redHsl = Color.hsl(0.degrees, 0.75, 0.5).toRgb
    val redRgb = Color.rgb(223.uByte, 32.uByte, 32.uByte)
    assert(redHsl ~= redRgb)
  }

  test("toOkLCh should convert to expected OkLCh color") {
    // Oklch values obtained from https://oklch.com
    val redRgb = Color.rgb(255.uByte, 0.uByte, 0.uByte)
    val redOkLCh = Color.oklch(0.6279, 0.2577, 29.23.degrees)
    assertEquals(redRgb, redOkLCh.toRgb)

    val greenRgb = Color.rgb(0.uByte, 255.uByte, 0.uByte)
    val greenOkLCh = Color.oklch(0.8664, 0.294827, 142.49.degrees)
    assert(greenRgb ~= greenOkLCh.toRgb)

    val blueRgb = Color.rgb(0.uByte, 0.uByte, 255.uByte)
    val blueOkLCh = Color.oklch(0.4520, 0.313214, 264.052.degrees)
    assert(blueRgb ~= blueOkLCh.toRgb)
  }

  test("Hsl with 0 saturation should convert to gray Rgb") {
    val grey1Hsl = Color.hsl(0.degrees, 0, 0.5).toRgb
    val grey1Rgb = Color.rgb(128.uByte, 128.uByte, 128.uByte)
    assert(grey1Hsl ~= grey1Rgb)

    val grey2Hsl = Color.hsl(0.degrees, 0, 1.0).toRgb
    val grey2Rgb = Color.rgb(255.uByte, 255.uByte, 255.uByte)
    assert(grey2Hsl ~= grey2Rgb)
  }

  test("Hsl spin should transform correctly") {
    val original = Color.oklch(0.5, 0.5, 180.degrees)
    val spun = original.spin(60.degrees)
    val unspun = original.spin(-60.degrees)

    assertEquals(spun.hue, 180.degrees + 60.degrees)
    assertEquals(spun.lightness, original.lightness)
    assertEquals(spun.saturation, original.saturation)
    assertEquals(unspun.hue, 180.degrees - 60.degrees)
    assertEquals(unspun.lightness, original.lightness)
    assertEquals(unspun.saturation, original.saturation)
  }

  test("Fade in/out should transform correctly") {
    val original = Color.hsl(120.degrees, 0.5, 0.5, 0.5)
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
