# Blend Modes

The SVG backend supports CSS-style blend modes for creating compositing effects.
Blend modes control how colors from one element interact with the colors beneath it,
enabling effects like screen blending, color burning, and more.

## How Blend Modes Work

Blend modes use the CSS `mix-blend-mode` property to determine how a drawing element's
colors are merged with the background. This allows you to create effects without needing
to manipulate pixel values directly.

## Using Blend Modes


The following blend mode extension methods are available on `Picture`:

- `.screen` — Lightens by inverting, multiplying, and inverting again
- `.burn` — Darkens by inverting the blend
- `.dodge` — Lightens by inverting the base
- `.lighten` — Selects the lighter color
- `.sourceOver` — Standard opacity blending (default)


Here's a minimal usage example:

```scala
import doodle.core.*
import doodle.svg.*
import doodle.syntax.all.*

val red = Picture.circle(50).fillColor(Color.red).noStroke
val blue = Picture.circle(50).fillColor(Color.blue).noStroke.translateX(40)

// Apply screen blend mode to the blue circle
val blended = red.beside(blue.screen)
```

The rendered result will show the overlapping area with the blend effect applied,
lightening the colors where the circles overlap.

## Browser Rendering

Blend modes are rendered using native SVG and CSS support, ensuring good performance
and visual fidelity. They work in all modern browsers that support the `mix-blend-mode` CSS property.
