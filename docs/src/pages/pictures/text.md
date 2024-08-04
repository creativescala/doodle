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
```

The output is shown below.

@:image(hello.png) {
  alt = A picture showing the text "Hello from Doodle!"
  title = "Hello from Doodle!"
}

In addition to creating pictures containing text, it also allows specifying the @:api(doodle.core.Font) used for the text, via the `font` method.
In this example we use the default serif font, in bold weight and 24 point size.

```scala mdoc:silent
import doodle.core.font.{Font, FontSize}

val font =
  Picture
    .text("Change the font")
    .font(Font.defaultSerif.bold.size(FontSize.points(24)))
```

This produces the picture below.

@:image(font.png) {
  alt = A picture showing the text "Change the font"
  title = "Change the font"
}


## Implementation

These methods are available on both the @:api(doodle.algebra.Text) algebra and @:api(doodle.image.Image).
