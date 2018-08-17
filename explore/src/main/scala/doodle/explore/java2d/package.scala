package doodle
package explore

import magnolia._
import javax.swing.{Box,JComponent}
import monix.reactive.Observable
import scala.language.experimental.macros

package object java2d extends Java2dExplorerAtoms {
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
}
