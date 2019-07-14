package doodle
package interact

package object syntax
    extends AnimationRendererSyntax
    with AnimationWriterSyntax
    with MouseMoveSyntax
    with MouseOverSyntax {
  object mouseMove extends MouseMoveSyntax
  object mouseOver extends MouseOverSyntax
  object animationRenderer extends AnimationRendererSyntax
  object animationWriter extends AnimationWriterSyntax
}
