package doodle
package interact

package object syntax
    extends AnimationRendererSyntax
    with AnimationWriterSyntax
    with InterpolationSyntax
    with MouseMoveSyntax
    with MouseOverSyntax
    with RedrawSyntax {
  object animationRenderer extends AnimationRendererSyntax
  object animationWriter extends AnimationWriterSyntax
  object interpolation extends InterpolationSyntax
  object mouseMove extends MouseMoveSyntax
  object mouseOver extends MouseOverSyntax
  object redraw extends RedrawSyntax
}
