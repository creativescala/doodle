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

package doodle.examples.canvas

import cats.effect.unsafe.implicits.global
import doodle.canvas.{*, given}
import doodle.canvas.algebra.Immediate
import doodle.core.*
import doodle.syntax.all.*
import org.scalajs.dom.CanvasRenderingContext2D
import scala.annotation.tailrec

import scala.scalajs.js.annotation.*

@JSExportTopLevel("CanvasImmediateMode")
object Experiment {

  def roof =
    Picture
      .triangle(200, 100)
      .fillColor(Color.blue)
      .strokeColor(Color.black)
      .debug

  def drawHotel =
    (ctx: Immediate) => {

      ctx.rectangle(25, 0, 150, 200)
      ctx.fill(Color.rgb(142, 113, 88))
      ctx.stroke(Color.black)

      ctx.rectangle(45, 0, 20, 40)
      ctx.fill(Color.rgb(72, 36, 20))
      @tailrec
      def drawWindows(index: Int): Unit = {
        if index < 12 then {
          val row = index / 4
          val col = index % 4
          val x = 30 + col * 40
          val y = 160 - row * 40

          val color = (index, col) match {
            case (0 | 1 | 4 | 5 | 8, _) => Color.rgb(255, 174, 0)
            case (2 | 3 | 6 | 9, _)     => Color.black
            case _                      => Color.rgb(255, 174, 0)
          }

          ctx.square(x, y, 20)
          ctx.fill(color)

          drawWindows(index + 1)
        }
      }

      drawWindows(0)

      ctx.stroke(Color.black)
      ctx.rectangle(175, 0, 50, 200)
      ctx.fill(Color.rgb(105, 66, 43))
      @tailrec
      def drawLines(y: Int): Unit = {
        if y >= 0 then {
          ctx.lineTo(175, y, 225, y)
          drawLines(y - 25)
        }
      }

      drawLines(165)

      ctx.ellipse(130, 35, 75, 50, Array(5, 5))
      ctx.fill(Color.rgb(234, 215, 163))

      ctx.text("HOTEL", 130, 32, font = "20px Fraktur")
    }

  def drawApartments =
    (ctx: Immediate) =>
      {
        @tailrec
        def drawBuildings(index: Int): Unit = {
          if index < 5 then {
            val x = index * 50
            val height = index match {
              case 0     => 200
              case 1 | 3 => 150
              case 2     => 175
              case 4     => 200
            }

            ctx.rectangle(x, 0, 50, height)
            ctx.fill(Color.rgb(178, 165, 148))
            ctx.stroke(Color.black)

            @tailrec
            def drawWindows(row: Int, col: Int): Unit = {
              if row < 4 && col < 2 then {
                val windowX = x + col * 20 + 5
                val windowY = height - (row + 1) * 40 + 10
                if windowY > 60 then {
                  ctx.rectangle(windowX, windowY, 15, 25)
                  ctx.fill(Color.rgb(220, 220, 220))
                  ctx.stroke(Color.black)
                }
                if col < 1 then drawWindows(row, col + 1)
                else drawWindows(row + 1, 0)
              }
            }

            drawWindows(0, 0)
            drawBuildings(index + 1)
          }
        }

        @tailrec
        def drawShops(index: Int): Unit = {
          if index < 5 then {
            val x = index * 50
            val color = index match {
              case 0 => Color.red
              case 1 => Color.rgb(39, 89, 70)
              case 2 => Color.rgb(59, 31, 92)
              case 3 => Color.rgb(133, 36, 81)
              case 4 => Color.rgb(59, 31, 92)
            }

            ctx.rectangle(x, 40, 50, 10)
            ctx.fill(color)
            ctx.stroke(Color.black)

            ctx.rectangle(x, 0, 50, 40)
            ctx.fill(Color.rgb(204, 239, 234))
            ctx.stroke(Color.black)

            drawShops(index + 1)
          }
        }

        drawBuildings(0)
        drawShops(0)

        ctx.clipRect(150, 150, 50, 50)
        ctx.clipRect(50, 150, 50, 50)
        ctx.clip()

        ctx.circle(100, 150, 50)
        ctx.fill(Color.rgb(178, 165, 148))
        ctx.stroke(Color.black)
        ctx.circle(150, 150, 50)
        ctx.fill(Color.rgb(178, 165, 148))
        ctx.stroke(Color.black)

        ctx.endClip()

        ctx.lineTo(100, 150, 100, 200)
        ctx.lineTo(100, 150, 50, 150)
        ctx.lineTo(150, 150, 150, 200)
        ctx.lineTo(150, 150, 200, 150)
      }

      def drawCitySpace =
        (ctx: Immediate) => {
          ctx.rectangle(0, 0, 130, 200)
          ctx.fill(Color.rgb(185, 185, 185))
          ctx.stroke(Color.black)
          ctx.rectangle(10, 130, 50, 60)
          ctx.fill(Color.rgb(162, 0, 0))
          ctx.stroke(Color.black)
          ctx.rectangle(70, 130, 50, 60)
          ctx.fill(Color.black);
          ctx.text("CITY SPACE", 65, 100, font = "20px Fraktur")

          ctx.rectangle(10, 60, 110, 30)
          ctx.fill(Color.rgb(204, 239, 234))
          ctx.stroke(Color.black)

          ctx.rectangle(10, 20, 30, 30)
          ctx.fill(Color.rgb(204, 239, 234))
          ctx.stroke(Color.black)
          ctx.rectangle(50, 0, 30, 50)
          ctx.fill(Color.rgb(204, 239, 234))
          ctx.stroke(Color.black)
          ctx.rectangle(90, 20, 30, 30)
          ctx.fill(Color.rgb(204, 239, 234))
          ctx.stroke(Color.black)
        }

      def drawRoad =
        (ctx: Immediate) => {
          ctx.rectangle(0, 0, 800, 50)
          ctx.fill(Color.rgb(135, 135, 144))
          ctx.stroke(Color.black)
          ctx.dashLine(0, 25, 800, 25, Array(5, 5))
        }

      val citySpace = raster(150, 200)(drawCitySpace)
      val hotel = raster(240, 200)(drawHotel)
      val apartments = raster(250, 200)(drawApartments)
      val road = raster(800, 50)(drawRoad)

      val tree =
        Picture
          .triangle(100, 100)
          .fillColor(Color.green)
          .strokeColor(Color.black)
          .above(
            Picture
              .rectangle(20, 50)
              .fillColor(Color.brown)
              .strokeColor(Color.black)
              .beside(
                Picture
                  .rectangle(20, 50)
                  .fillColor(Color.brown)
                  .strokeColor(Color.black)
              )
          )
          .at(0, -120)
          .scale(0.5, 0.5)

      val joint = (citySpace
        .beside(apartments.beside(hotel.beside(tree).beside(tree))))
        .above(road)

      @JSExport
      def draw(mount: String) =
        joint.drawWithFrame(Frame(mount))
}
