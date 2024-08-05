# Text

## Concept

Adding text to pictures is done with the @:api(doodle.algebra.Text) algebra.

Here's a very simple example creating some text using the `text` constructor.

```scala mdoc:silent
import doodle.core.*
import doodle.java2d.*
import doodle.syntax.all.*

val hello =
  Picture
    .text("Hello from Doodle!")
    .fillColor(Color.black)
```

The output is shown below.

@:doodle("hello-example", "SvgTextExamples.drawHello") 

In addition to creating pictures containing text, it also allows specifying the @:api(doodle.core.Font) used for the text, via the `font` method.
In this example we use the default serif font, bold weight, 24 point size, and some questionable color choices.

```scala mdoc:silent
import doodle.core.font.{Font, FontSize}

val font =
  Picture
    .text("Change the font")
    .strokeColor(Color.blueViolet)
    .fillColor(Color.royalBlue)
    .font(Font.defaultSerif.bold.size(FontSize.points(24)))
```

This produces the picture below.

@:doodle("font-example", "SvgTextExamples.drawFont") 


## Implementation

These methods are available on both the @:api(doodle.algebra.Text) algebra and @:api(doodle.image.Image).
