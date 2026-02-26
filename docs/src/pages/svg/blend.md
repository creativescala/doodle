# Blend Modes

The SVG backend supports [CSS-style blend modes][css-blend] for creating compositing effects.
Blend modes control how colors from one element interact with the colors beneath it.
By default an color simply overwrites what is beneath, 
but by changing the blend mode you can make the two colors interact in creative ways.


## Using Blend Modes

The SVG backend supports the following blend modes:

- screen: lightens by inverting, multiplying, and inverting again.
- burn: darkens by inverting the bottom, dividing by the top, and inverting again. Corresponds to CSS `color-burn`.
- dodge: divide the bottom by the inverse of the top. Corresponds to CSS `color-dodge`.
- lighten: selects the lighter color.
- sourceOver: the default, in which the top color overwrites whatever is below it. Corresponds to CSS `normal`.

Here's an example showing three polygons blended together with `screen` and `dodge`.

```scala mdoc:silent
import doodle.core.*
import doodle.svg.*
import doodle.syntax.all.*

val red = Picture
  .regularPolygon(5, 50)
  .fillColor(Color.indianRed)
  .strokeColor(Color.mediumPurple)
  .strokeWidth(7)

val green = Picture
  .regularPolygon(7, 50)
  .fillColor(Color.lawnGreen)
  .strokeColor(Color.forestGreen)
  .strokeWidth(7)

val blue = Picture
  .regularPolygon(9, 50)
  .fillColor(Color.dodgerBlue)
  .strokeColor(Color.lavenderBlush)
  .strokeWidth(7)

red
  .at(Landmark.centerLeft)
  .screen
  .on(blue.at(Landmark.centerRight).dodge)
  .on(green)
```

This produces the following:

@:doodle(polygons, SvgBlendModes.polygons)

The following picture shows examples of all the blend modes, where the red and blue circles are placed on top of the green circle and both blended with the given mode.

@:doodle(blend-table, SvgBlendModes.table)


## Implementation Notes

Blend modes are rendered using the [CSS `mix-blend-mode`][mix-blend-mode] property. At the time of writing there are still a few browsers, mainly on mobile devices, that don't support this feature on SVG elements. Check [compatibility] for your use case.


[css-blend]: https://developer.mozilla.org/en-US/docs/Web/CSS/Reference/Values/blend-mode
[mix-blend-mode]: https://developer.mozilla.org/en-US/docs/Web/CSS/Reference/Properties/mix-blend-mode
[compatibility]: https://developer.mozilla.org/en-US/docs/Web/CSS/Reference/Properties/mix-blend-mode#browser_compatibility
