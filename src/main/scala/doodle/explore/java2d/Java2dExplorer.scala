/*
 * Copyright 2015 noelwelsh
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
package explore
package java2d

import cats.effect.IO
import doodle.core.Color
import javax.swing._
import javax.swing.event.{ChangeEvent, ChangeListener}
import monix.execution.{Ack, Scheduler}
import monix.reactive._
import monix.reactive.subjects.Var
import magnolia._
import scala.language.experimental.macros

trait Java2dExplorer[A] extends Explorer[JComponent,A] {
  def render: IO[Observable[A]] =
    IO {
      val frame = new JFrame("Explorer")
      frame.getContentPane().add(ui)
      frame.pack()
      frame.setVisible(true)
      frame.repaint()
      frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)

      value
    }
}
/** Generic derivation of Explorer with Java2D interface. */
object Java2dExplorer {
  implicit val java2dExplorerScheduler: Scheduler =
    Scheduler.fixedPool("Java2dExplorer", 1)

  type Typeclass[A] = ExplorerFactory[JComponent,A]
  def combine[A](caseClass: CaseClass[Typeclass, A]): Typeclass[A] =
    new ExplorerFactory[JComponent,A] {
      def create =
        new Java2dExplorer[A] {
          val children = caseClass.parameters.map(p => p.typeclass.create)

          val ui: JComponent = {
            val container = Box.createVerticalBox()
            children.foreach(c => container.add(c.ui, -1))
            container.revalidate()
            container
          }

          val value: Observable[A] = {
            Observable
              .combineLatestList(children.map(c => c.value): _*)
              .map(v => caseClass.rawConstruct(v))
          }
        }
    }

  def dispatch[A](sealedTrait: SealedTrait[Typeclass, A]): Typeclass[A] = ???

  implicit def gen[A]: Typeclass[A] = macro Magnolia.gen[A]

  /** Explore Double with a Java2D interface */
  implicit val doubleExplorer =
    new ExplorerFactory[JComponent,Double] {
      def create =
        new Java2dExplorer[Double] {
          val ui: JSlider = new JSlider(-100, 100)
          val value: Var[Double] = Var(ui.getValue().toDouble)

          ui.addChangeListener(
            new ChangeListener {
              def stateChanged(evt: ChangeEvent): Unit =
                value := (ui.getValue().toDouble) match {
                  case Ack.Continue => ()
                  case Ack.Stop => ui.setEnabled(false)
                }
            }
          )
        }
    }

  /** Explore Color with a Java2D interface */
  implicit val colorExplorer =
    new ExplorerFactory[JComponent,Color] {
      def create =
        new Java2dExplorer[Color] {
          val ui = new JColorChooser(java.awt.Color.black)
          val source = Var(ui.getColor)
          val value = source.map(c => Color.rgba(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha().toDouble / 255.0))

          ui.getSelectionModel().addChangeListener(
            new ChangeListener {
              def stateChanged(evt: ChangeEvent): Unit =
                source := (ui.getColor()) match {
                  case Ack.Continue => ()
                  case Ack.Stop => ui.setEnabled(false)
                }
            }
          )
        }
    }

}
