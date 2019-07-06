package doodle
package interact

package object syntax
    extends AnimatorSyntax
    with MouseMoveSyntax
    with MouseOverSyntax {
  object mouseMove extends MouseMoveSyntax
  object mouseOver extends MouseOverSyntax
  object animator extends AnimatorSyntax
}
