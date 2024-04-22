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

package docs
package pictures

import cats.effect.unsafe.implicits.global
import doodle.core.*
import doodle.java2d.*
import doodle.syntax.all.*


object Transform {
    val hut = 
    Picture
        .triangle(50, 50)
        .fillColor(Color.black)
        .strokeColor(Color.red)
        .above(Picture.rectangle(50, 50).fillColor(Color.blue))

    val rotatedHut = hut.rotate(45.degrees)
    val scaledHut = hut.scale(1.5, 1.5)
    val translatedHut = hut.translate(500, 50)
    val verticallyReflectedHut = hut.verticalReflection

    rotatedHut.save("pictures/rotated-hut.png")
    scaledHut.save("pictures/scaled-hut.png")
    translatedHut.save("pictures/translated-hut.png")
    verticallyReflectedHut.save("pictures/vertically-reflected-hut.png")

}
