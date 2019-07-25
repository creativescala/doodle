package doodle
package interact

package object syntax
    extends AnimationRendererSyntax
    with AnimationWriterSyntax
    with MouseMoveSyntax
    with MouseOverSyntax
    with RedrawSyntax{
  object mouseMove extends MouseMoveSyntax
  object mouseOver extends MouseOverSyntax
  object redraw extends RedrawSyntax
  object animationRenderer extends AnimationRendererSyntax
  object animationWriter extends AnimationWriterSyntax
}
