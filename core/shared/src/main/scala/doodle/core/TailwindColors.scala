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

package doodle.core

import doodle.syntax.all.*

/** The Tailwind CSS 4.0 default color palette. See
  * https://tailwindcss.com/docs/colors
  */
object TailwindColors {
  // These colors are used under the terms of Tailwind CSS license, repeated below.
  //
  // MIT License
  // Copyright (c) Tailwind Labs, Inc.

  // Permission is hereby granted, free of charge, to any person obtaining a copy
  // of this software and associated documentation files (the "Software") to deal
  // in the Software without restriction, including without limitation the rights
  // to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  // copies of the Software, and to permit persons to whom the Software is
  // furnished to do so, subject to the following conditions:

  // The above copyright notice and this permission notice shall be included in all
  // copies or substantial portions of the Software.

  // THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  // IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  // FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  // AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  // LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  // OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  // SOFTWARE.
  val red50: Color = Color.oklch(0.971, 0.013, 17.38.degrees)
  val red100: Color = Color.oklch(0.936, 0.032, 17.717.degrees)
  val red200: Color = Color.oklch(0.885, 0.062, 18.334.degrees)
  val red300: Color = Color.oklch(0.808, 0.114, 19.571.degrees)
  val red400: Color = Color.oklch(0.704, 0.191, 22.216.degrees)
  val red500: Color = Color.oklch(0.637, 0.237, 25.331.degrees)
  val red600: Color = Color.oklch(0.577, 0.245, 27.325.degrees)
  val red700: Color = Color.oklch(0.505, 0.213, 27.518.degrees)
  val red800: Color = Color.oklch(0.444, 0.177, 26.899.degrees)
  val red900: Color = Color.oklch(0.396, 0.141, 25.723.degrees)
  val red950: Color = Color.oklch(0.258, 0.092, 26.042.degrees)
  val orange50: Color = Color.oklch(0.98, 0.016, 73.684.degrees)
  val orange100: Color = Color.oklch(0.954, 0.038, 75.164.degrees)
  val orange200: Color = Color.oklch(0.901, 0.076, 70.697.degrees)
  val orange300: Color = Color.oklch(0.837, 0.128, 66.29.degrees)
  val orange400: Color = Color.oklch(0.75, 0.183, 55.934.degrees)
  val orange500: Color = Color.oklch(0.705, 0.213, 47.604.degrees)
  val orange600: Color = Color.oklch(0.646, 0.222, 41.116.degrees)
  val orange700: Color = Color.oklch(0.553, 0.195, 38.402.degrees)
  val orange800: Color = Color.oklch(0.47, 0.157, 37.304.degrees)
  val orange900: Color = Color.oklch(0.408, 0.123, 38.172.degrees)
  val orange950: Color = Color.oklch(0.266, 0.079, 36.259.degrees)
  val amber50: Color = Color.oklch(0.987, 0.022, 95.277.degrees)
  val amber100: Color = Color.oklch(0.962, 0.059, 95.617.degrees)
  val amber200: Color = Color.oklch(0.924, 0.12, 95.746.degrees)
  val amber300: Color = Color.oklch(0.879, 0.169, 91.605.degrees)
  val amber400: Color = Color.oklch(0.828, 0.189, 84.429.degrees)
  val amber500: Color = Color.oklch(0.769, 0.188, 70.08.degrees)
  val amber600: Color = Color.oklch(0.666, 0.179, 58.318.degrees)
  val amber700: Color = Color.oklch(0.555, 0.163, 48.998.degrees)
  val amber800: Color = Color.oklch(0.473, 0.137, 46.201.degrees)
  val amber900: Color = Color.oklch(0.414, 0.112, 45.904.degrees)
  val amber950: Color = Color.oklch(0.279, 0.077, 45.635.degrees)
  val yellow50: Color = Color.oklch(0.987, 0.026, 102.212.degrees)
  val yellow100: Color = Color.oklch(0.973, 0.071, 103.193.degrees)
  val yellow200: Color = Color.oklch(0.945, 0.129, 101.54.degrees)
  val yellow300: Color = Color.oklch(0.905, 0.182, 98.111.degrees)
  val yellow400: Color = Color.oklch(0.852, 0.199, 91.936.degrees)
  val yellow500: Color = Color.oklch(0.795, 0.184, 86.047.degrees)
  val yellow600: Color = Color.oklch(0.681, 0.162, 75.834.degrees)
  val yellow700: Color = Color.oklch(0.554, 0.135, 66.442.degrees)
  val yellow800: Color = Color.oklch(0.476, 0.114, 61.907.degrees)
  val yellow900: Color = Color.oklch(0.421, 0.095, 57.708.degrees)
  val yellow950: Color = Color.oklch(0.286, 0.066, 53.813.degrees)
  val lime50: Color = Color.oklch(0.986, 0.031, 120.757.degrees)
  val lime100: Color = Color.oklch(0.967, 0.067, 122.328.degrees)
  val lime200: Color = Color.oklch(0.938, 0.127, 124.321.degrees)
  val lime300: Color = Color.oklch(0.897, 0.196, 126.665.degrees)
  val lime400: Color = Color.oklch(0.841, 0.238, 128.85.degrees)
  val lime500: Color = Color.oklch(0.768, 0.233, 130.85.degrees)
  val lime600: Color = Color.oklch(0.648, 0.2, 131.684.degrees)
  val lime700: Color = Color.oklch(0.532, 0.157, 131.589.degrees)
  val lime800: Color = Color.oklch(0.453, 0.124, 130.933.degrees)
  val lime900: Color = Color.oklch(0.405, 0.101, 131.063.degrees)
  val lime950: Color = Color.oklch(0.274, 0.072, 132.109.degrees)
  val green50: Color = Color.oklch(0.982, 0.018, 155.826.degrees)
  val green100: Color = Color.oklch(0.962, 0.044, 156.743.degrees)
  val green200: Color = Color.oklch(0.925, 0.084, 155.995.degrees)
  val green300: Color = Color.oklch(0.871, 0.15, 154.449.degrees)
  val green400: Color = Color.oklch(0.792, 0.209, 151.711.degrees)
  val green500: Color = Color.oklch(0.723, 0.219, 149.579.degrees)
  val green600: Color = Color.oklch(0.627, 0.194, 149.214.degrees)
  val green700: Color = Color.oklch(0.527, 0.154, 150.069.degrees)
  val green800: Color = Color.oklch(0.448, 0.119, 151.328.degrees)
  val green900: Color = Color.oklch(0.393, 0.095, 152.535.degrees)
  val green950: Color = Color.oklch(0.266, 0.065, 152.934.degrees)
  val emerald50: Color = Color.oklch(0.979, 0.021, 166.113.degrees)
  val emerald100: Color = Color.oklch(0.95, 0.052, 163.051.degrees)
  val emerald200: Color = Color.oklch(0.905, 0.093, 164.15.degrees)
  val emerald300: Color = Color.oklch(0.845, 0.143, 164.978.degrees)
  val emerald400: Color = Color.oklch(0.765, 0.177, 163.223.degrees)
  val emerald500: Color = Color.oklch(0.696, 0.17, 162.48.degrees)
  val emerald600: Color = Color.oklch(0.596, 0.145, 163.225.degrees)
  val emerald700: Color = Color.oklch(0.508, 0.118, 165.612.degrees)
  val emerald800: Color = Color.oklch(0.432, 0.095, 166.913.degrees)
  val emerald900: Color = Color.oklch(0.378, 0.077, 168.94.degrees)
  val emerald950: Color = Color.oklch(0.262, 0.051, 172.552.degrees)
  val teal50: Color = Color.oklch(0.984, 0.014, 180.72.degrees)
  val teal100: Color = Color.oklch(0.953, 0.051, 180.801.degrees)
  val teal200: Color = Color.oklch(0.91, 0.096, 180.426.degrees)
  val teal300: Color = Color.oklch(0.855, 0.138, 181.071.degrees)
  val teal400: Color = Color.oklch(0.777, 0.152, 181.912.degrees)
  val teal500: Color = Color.oklch(0.704, 0.14, 182.503.degrees)
  val teal600: Color = Color.oklch(0.6, 0.118, 184.704.degrees)
  val teal700: Color = Color.oklch(0.511, 0.096, 186.391.degrees)
  val teal800: Color = Color.oklch(0.437, 0.078, 188.216.degrees)
  val teal900: Color = Color.oklch(0.386, 0.063, 188.416.degrees)
  val teal950: Color = Color.oklch(0.277, 0.046, 192.524.degrees)
  val cyan50: Color = Color.oklch(0.984, 0.019, 200.873.degrees)
  val cyan100: Color = Color.oklch(0.956, 0.045, 203.388.degrees)
  val cyan200: Color = Color.oklch(0.917, 0.08, 205.041.degrees)
  val cyan300: Color = Color.oklch(0.865, 0.127, 207.078.degrees)
  val cyan400: Color = Color.oklch(0.789, 0.154, 211.53.degrees)
  val cyan500: Color = Color.oklch(0.715, 0.143, 215.221.degrees)
  val cyan600: Color = Color.oklch(0.609, 0.126, 221.723.degrees)
  val cyan700: Color = Color.oklch(0.52, 0.105, 223.128.degrees)
  val cyan800: Color = Color.oklch(0.45, 0.085, 224.283.degrees)
  val cyan900: Color = Color.oklch(0.398, 0.07, 227.392.degrees)
  val cyan950: Color = Color.oklch(0.302, 0.056, 229.695.degrees)
  val sky50: Color = Color.oklch(0.977, 0.013, 236.62.degrees)
  val sky100: Color = Color.oklch(0.951, 0.026, 236.824.degrees)
  val sky200: Color = Color.oklch(0.901, 0.058, 230.902.degrees)
  val sky300: Color = Color.oklch(0.828, 0.111, 230.318.degrees)
  val sky400: Color = Color.oklch(0.746, 0.16, 232.661.degrees)
  val sky500: Color = Color.oklch(0.685, 0.169, 237.323.degrees)
  val sky600: Color = Color.oklch(0.588, 0.158, 241.966.degrees)
  val sky700: Color = Color.oklch(0.5, 0.134, 242.749.degrees)
  val sky800: Color = Color.oklch(0.443, 0.11, 240.79.degrees)
  val sky900: Color = Color.oklch(0.391, 0.09, 240.876.degrees)
  val sky950: Color = Color.oklch(0.293, 0.066, 243.157.degrees)
  val blue50: Color = Color.oklch(0.97, 0.014, 254.604.degrees)
  val blue100: Color = Color.oklch(0.932, 0.032, 255.585.degrees)
  val blue200: Color = Color.oklch(0.882, 0.059, 254.128.degrees)
  val blue300: Color = Color.oklch(0.809, 0.105, 251.813.degrees)
  val blue400: Color = Color.oklch(0.707, 0.165, 254.624.degrees)
  val blue500: Color = Color.oklch(0.623, 0.214, 259.815.degrees)
  val blue600: Color = Color.oklch(0.546, 0.245, 262.881.degrees)
  val blue700: Color = Color.oklch(0.488, 0.243, 264.376.degrees)
  val blue800: Color = Color.oklch(0.424, 0.199, 265.638.degrees)
  val blue900: Color = Color.oklch(0.379, 0.146, 265.522.degrees)
  val blue950: Color = Color.oklch(0.282, 0.091, 267.935.degrees)
  val indigo50: Color = Color.oklch(0.962, 0.018, 272.314.degrees)
  val indigo100: Color = Color.oklch(0.93, 0.034, 272.788.degrees)
  val indigo200: Color = Color.oklch(0.87, 0.065, 274.039.degrees)
  val indigo300: Color = Color.oklch(0.785, 0.115, 274.713.degrees)
  val indigo400: Color = Color.oklch(0.673, 0.182, 276.935.degrees)
  val indigo500: Color = Color.oklch(0.585, 0.233, 277.117.degrees)
  val indigo600: Color = Color.oklch(0.511, 0.262, 276.966.degrees)
  val indigo700: Color = Color.oklch(0.457, 0.24, 277.023.degrees)
  val indigo800: Color = Color.oklch(0.398, 0.195, 277.366.degrees)
  val indigo900: Color = Color.oklch(0.359, 0.144, 278.697.degrees)
  val indigo950: Color = Color.oklch(0.257, 0.09, 281.288.degrees)
  val violet50: Color = Color.oklch(0.969, 0.016, 293.756.degrees)
  val violet100: Color = Color.oklch(0.943, 0.029, 294.588.degrees)
  val violet200: Color = Color.oklch(0.894, 0.057, 293.283.degrees)
  val violet300: Color = Color.oklch(0.811, 0.111, 293.571.degrees)
  val violet400: Color = Color.oklch(0.702, 0.183, 293.541.degrees)
  val violet500: Color = Color.oklch(0.606, 0.25, 292.717.degrees)
  val violet600: Color = Color.oklch(0.541, 0.281, 293.009.degrees)
  val violet700: Color = Color.oklch(0.491, 0.27, 292.581.degrees)
  val violet800: Color = Color.oklch(0.432, 0.232, 292.759.degrees)
  val violet900: Color = Color.oklch(0.38, 0.189, 293.745.degrees)
  val violet950: Color = Color.oklch(0.283, 0.141, 291.089.degrees)
  val purple50: Color = Color.oklch(0.977, 0.014, 308.299.degrees)
  val purple100: Color = Color.oklch(0.946, 0.033, 307.174.degrees)
  val purple200: Color = Color.oklch(0.902, 0.063, 306.703.degrees)
  val purple300: Color = Color.oklch(0.827, 0.119, 306.383.degrees)
  val purple400: Color = Color.oklch(0.714, 0.203, 305.504.degrees)
  val purple500: Color = Color.oklch(0.627, 0.265, 303.9.degrees)
  val purple600: Color = Color.oklch(0.558, 0.288, 302.321.degrees)
  val purple700: Color = Color.oklch(0.496, 0.265, 301.924.degrees)
  val purple800: Color = Color.oklch(0.438, 0.218, 303.724.degrees)
  val purple900: Color = Color.oklch(0.381, 0.176, 304.987.degrees)
  val purple950: Color = Color.oklch(0.291, 0.149, 302.717.degrees)
  val fuchsia50: Color = Color.oklch(0.977, 0.017, 320.058.degrees)
  val fuchsia100: Color = Color.oklch(0.952, 0.037, 318.852.degrees)
  val fuchsia200: Color = Color.oklch(0.903, 0.076, 319.62.degrees)
  val fuchsia300: Color = Color.oklch(0.833, 0.145, 321.434.degrees)
  val fuchsia400: Color = Color.oklch(0.74, 0.238, 322.16.degrees)
  val fuchsia500: Color = Color.oklch(0.667, 0.295, 322.15.degrees)
  val fuchsia600: Color = Color.oklch(0.591, 0.293, 322.896.degrees)
  val fuchsia700: Color = Color.oklch(0.518, 0.253, 323.949.degrees)
  val fuchsia800: Color = Color.oklch(0.452, 0.211, 324.591.degrees)
  val fuchsia900: Color = Color.oklch(0.401, 0.17, 325.612.degrees)
  val fuchsia950: Color = Color.oklch(0.293, 0.136, 325.661.degrees)
  val pink50: Color = Color.oklch(0.971, 0.014, 343.198.degrees)
  val pink100: Color = Color.oklch(0.948, 0.028, 342.258.degrees)
  val pink200: Color = Color.oklch(0.899, 0.061, 343.231.degrees)
  val pink300: Color = Color.oklch(0.823, 0.12, 346.018.degrees)
  val pink400: Color = Color.oklch(0.718, 0.202, 349.761.degrees)
  val pink500: Color = Color.oklch(0.656, 0.241, 354.308.degrees)
  val pink600: Color = Color.oklch(0.592, 0.249, 0.584.degrees)
  val pink700: Color = Color.oklch(0.525, 0.223, 3.958.degrees)
  val pink800: Color = Color.oklch(0.459, 0.187, 3.815.degrees)
  val pink900: Color = Color.oklch(0.408, 0.153, 2.432.degrees)
  val pink950: Color = Color.oklch(0.284, 0.109, 3.907.degrees)
  val rose50: Color = Color.oklch(0.969, 0.015, 12.422.degrees)
  val rose100: Color = Color.oklch(0.941, 0.03, 12.58.degrees)
  val rose200: Color = Color.oklch(0.892, 0.058, 10.001.degrees)
  val rose300: Color = Color.oklch(0.81, 0.117, 11.638.degrees)
  val rose400: Color = Color.oklch(0.712, 0.194, 13.428.degrees)
  val rose500: Color = Color.oklch(0.645, 0.246, 16.439.degrees)
  val rose600: Color = Color.oklch(0.586, 0.253, 17.585.degrees)
  val rose700: Color = Color.oklch(0.514, 0.222, 16.935.degrees)
  val rose800: Color = Color.oklch(0.455, 0.188, 13.697.degrees)
  val rose900: Color = Color.oklch(0.41, 0.159, 10.272.degrees)
  val rose950: Color = Color.oklch(0.271, 0.105, 12.094.degrees)
  val slate50: Color = Color.oklch(0.984, 0.003, 247.858.degrees)
  val slate100: Color = Color.oklch(0.968, 0.007, 247.896.degrees)
  val slate200: Color = Color.oklch(0.929, 0.013, 255.508.degrees)
  val slate300: Color = Color.oklch(0.869, 0.022, 252.894.degrees)
  val slate400: Color = Color.oklch(0.704, 0.04, 256.788.degrees)
  val slate500: Color = Color.oklch(0.554, 0.046, 257.417.degrees)
  val slate600: Color = Color.oklch(0.446, 0.043, 257.281.degrees)
  val slate700: Color = Color.oklch(0.372, 0.044, 257.287.degrees)
  val slate800: Color = Color.oklch(0.279, 0.041, 260.031.degrees)
  val slate900: Color = Color.oklch(0.208, 0.042, 265.755.degrees)
  val slate950: Color = Color.oklch(0.129, 0.042, 264.695.degrees)
  val gray50: Color = Color.oklch(0.985, 0.002, 247.839.degrees)
  val gray100: Color = Color.oklch(0.967, 0.003, 264.542.degrees)
  val gray200: Color = Color.oklch(0.928, 0.006, 264.531.degrees)
  val gray300: Color = Color.oklch(0.872, 0.01, 258.338.degrees)
  val gray400: Color = Color.oklch(0.707, 0.022, 261.325.degrees)
  val gray500: Color = Color.oklch(0.551, 0.027, 264.364.degrees)
  val gray600: Color = Color.oklch(0.446, 0.03, 256.802.degrees)
  val gray700: Color = Color.oklch(0.373, 0.034, 259.733.degrees)
  val gray800: Color = Color.oklch(0.278, 0.033, 256.848.degrees)
  val gray900: Color = Color.oklch(0.21, 0.034, 264.665.degrees)
  val gray950: Color = Color.oklch(0.13, 0.028, 261.692.degrees)
  val zinc50: Color = Color.oklch(0.985, 0, 0.degrees)
  val zinc100: Color = Color.oklch(0.967, 0.001, 286.375.degrees)
  val zinc200: Color = Color.oklch(0.92, 0.004, 286.32.degrees)
  val zinc300: Color = Color.oklch(0.871, 0.006, 286.286.degrees)
  val zinc400: Color = Color.oklch(0.705, 0.015, 286.067.degrees)
  val zinc500: Color = Color.oklch(0.552, 0.016, 285.938.degrees)
  val zinc600: Color = Color.oklch(0.442, 0.017, 285.786.degrees)
  val zinc700: Color = Color.oklch(0.37, 0.013, 285.805.degrees)
  val zinc800: Color = Color.oklch(0.274, 0.006, 286.033.degrees)
  val zinc900: Color = Color.oklch(0.21, 0.006, 285.885.degrees)
  val zinc950: Color = Color.oklch(0.141, 0.005, 285.823.degrees)
  val neutral50: Color = Color.oklch(0.985, 0, 0.degrees)
  val neutral100: Color = Color.oklch(0.97, 0, 0.degrees)
  val neutral200: Color = Color.oklch(0.922, 0, 0.degrees)
  val neutral300: Color = Color.oklch(0.87, 0, 0.degrees)
  val neutral400: Color = Color.oklch(0.708, 0, 0.degrees)
  val neutral500: Color = Color.oklch(0.556, 0, 0.degrees)
  val neutral600: Color = Color.oklch(0.439, 0, 0.degrees)
  val neutral700: Color = Color.oklch(0.371, 0, 0.degrees)
  val neutral800: Color = Color.oklch(0.269, 0, 0.degrees)
  val neutral900: Color = Color.oklch(0.205, 0, 0.degrees)
  val neutral950: Color = Color.oklch(0.145, 0, 0.degrees)
  val stone50: Color = Color.oklch(0.985, 0.001, 106.423.degrees)
  val stone100: Color = Color.oklch(0.97, 0.001, 106.424.degrees)
  val stone200: Color = Color.oklch(0.923, 0.003, 48.717.degrees)
  val stone300: Color = Color.oklch(0.869, 0.005, 56.366.degrees)
  val stone400: Color = Color.oklch(0.709, 0.01, 56.259.degrees)
  val stone500: Color = Color.oklch(0.553, 0.013, 58.071.degrees)
  val stone600: Color = Color.oklch(0.444, 0.011, 73.639.degrees)
  val stone700: Color = Color.oklch(0.374, 0.01, 67.558.degrees)
  val stone800: Color = Color.oklch(0.268, 0.007, 34.298.degrees)
  val stone900: Color = Color.oklch(0.216, 0.006, 56.043.degrees)
  val stone950: Color = Color.oklch(0.147, 0.004, 49.25.degrees)
}
