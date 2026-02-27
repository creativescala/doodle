# Blend Modes

The Canvas backend supports blend modes via the Canvas 2D API's `globalCompositeOperation` property.
See the [general blend mode documentation](../pictures/blend.md) for a description of the available blend modes and usage examples.


## Implementation Notes

Canvas blending currently does not work as intended when pictures have both a stroke and a fill. This is because the Canvas 2D API applies blend modes relative to the entire canvas, and stroking and filling a picture are each separate operations. The consequence is that the stroke blends with the fill. Correct per-layer blending requires `beginLayer` and `endLayer` support, which is not yet available.

In the meantime, blend modes may still produce interesting effects, but the results may differ from what the SVG backend produces. Compare, for example, the canvas output 

@:doodle(canvas-polygons, CanvasBlendExamples.drawPolygons)

to the correct SVG output

@:doodle(svg-polygons, SvgBlendExamples.drawPolygons)
