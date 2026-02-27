# Blend Modes

Blend modes control how colors from one picture interact with the colors of pictures beneath it.
By default a color simply overwrites what is beneath it,
but by changing the blend mode you can make the two colors interact in creative ways.

Blend mode support is provided by the @:api(doodle.algebra.Blend) algebra.


## Using Blend Modes

The available blend modes are the same "Photoshop-like" modes defined in the [CSS blend modes][css-blend-modes] specification:

- `normal`: the default, in which the top color overwrites whatever is below it.
- `darken`: selects the darker of the two colors.
- `multiply`: multiplies the colors together, making them darker.
- `colorBurn`: darkens by inverting the bottom, dividing by the top, and inverting again. Corresponds to CSS `color-burn`.
- `lighten`: selects the lighter of the two colors.
- `screen`: lightens by inverting, multiplying, and inverting again.
- `colorDodge`: brightens by dividing the bottom by the inverse of the top. Corresponds to CSS `color-dodge`.
- `overlay`: multiplies or screens depending on the background color.
- `softLight`: a softer version of hard-light. Corresponds to CSS `soft-light`.
- `hardLight`: multiplies or screens depending on the source color. Corresponds to CSS `hard-light`.
- `difference`: subtracts the darker of the two colors from the lighter.
- `exclusion`: similar to difference but with lower contrast.
- `hue`: applies the hue of the top color with the saturation and luminosity of the bottom.
- `saturation`: applies the saturation of the top color with the hue and luminosity of the bottom.
- `color`: applies the hue and saturation of the top color with the luminosity of the bottom.
- `luminosity`: applies the luminosity of the top color with the hue and saturation of the bottom.

Here's an example showing three polygons blended together with `screen` and `colorDodge`.

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
  .on(blue.at(Landmark.centerRight).colorDodge)
  .on(green)
```

This produces the following:

@:doodle(polygons, SvgBlendExamples.drawPolygons)

The following picture shows examples of all the blend modes, where the red and blue circles are placed on top of the green circle and both blended with the given mode.

@:doodle(blend-table, SvgBlendExamples.drawTable)


## Backend Support

Blend modes are supported by the SVG and Canvas backends. See the backend-specific pages for implementation notes:

- [SVG blend modes](../svg/blend.md)
- [Canvas blend modes](../canvas/blend.md)


[css-blend-modes]: https://developer.mozilla.org/en-US/docs/Web/CSS/Reference/Properties/mix-blend-mode
