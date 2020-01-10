package doodle
package interact

package object syntax
    extends AnimationRendererSyntax
    with AnimationWriterSyntax
    with EnumeratorSyntax
    with MouseMoveSyntax
    with MouseOverSyntax
    with RedrawSyntax{
  object animationRenderer extends AnimationRendererSyntax
  object animationWriter extends AnimationWriterSyntax
  object enumerator extends EnumeratorSyntax
  object mouseMove extends MouseMoveSyntax
  object mouseOver extends MouseOverSyntax
  object redraw extends RedrawSyntax
}
