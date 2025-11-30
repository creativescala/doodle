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

hello.noStroke
  .fillColor(Color.black)
  .above(hello.noFill)
  .above(hello.fillColor(Color.black))
```

The output is shown below.

@:doodle("hello-example", "SvgTextExamples.drawHello") 

In addition to creating pictures containing text, it also allows specifying the @:api(doodle.core.Font) used for the text, via the `font` method.
In this example we use the default serif font, bold weight, 24 point size, and some questionable color choices.

```scala mdoc:silent
import doodle.core.font.{Font, FontSize}

Picture
  .text("Change the font")
  .strokeColor(Color.blueViolet)
  .fillColor(Color.royalBlue)
  .font(Font.defaultSerif.withBold.withSize(FontSize.points(24)))
```

This produces the picture below.

@:doodle("font-example", "SvgTextExamples.drawFont") 


## Fonts

The `Text` algebra works with a @:api(doodle.core.font.Font) to specify the font used to render the text. A `Font` has the following elements:

- the font family, such as Helvetica or Times New Roman, is what people often refer to as just the font;
- the style of the font, which is italic or normal;
- the weight of the font, which is bold or normal; and
- the size of the font, which is specified in points.

When constructing a `Font` all these parameters, apart from the font family, are optional. 
So, for example, you can create a `Font` with just the following:

```scala mdoc:silent
import doodle.core.font.FontFamily

Font(FontFamily.serif)
```

You can also take an existing font and use the builder methods to create a copy with changed properties. For example, here we take a font and change it's size and weight.

```scala mdoc:silent
Font.defaultSerif.withSize(20).withBold
```

Let's look at the elements of a font in a bit more detail.


### Font Families

There are three default font families, for serif, sans serif, and monospaced fonts. 
These are a good choice for cross platform applications, as the actual font is chosen by the platform. 

The simplest way to use these families is by calling `Font.defaultSerif`, `Font.defaultSansSerif`, and `Font.defaultMonospaced`. 
The example below shows the default font families in use.

```scala mdoc:silent
Picture.text("serif")
  .font(Font.defaultSerif)
  .margin(10)
  .beside(Picture.text("sans serif").font(Font.defaultSansSerif).margin(10))
  .beside(Picture.text("monospaced").font(Font.defaultMonospaced).margin(10))
  .noStroke
  .fillColor(Color.black)
```

Here's the output it produces. This output is drawn using the SVG backend, so the exact choice of fonts will be determined by the browser you use to view this page.

@:doodle("default-example", "SvgTextExamples.drawDefault") 

You may wish to specify an exact font family by it's `String` name. The interpretation of this name is down to the backend. For example, using the SVG backend means a [CSS font family](https://developer.mozilla.org/en-US/docs/Web/CSS/Reference/Properties/font-family).

This example code shows the use of four different font families.

```scala mdoc:silent
Picture.text("Helvetica")
  .font(Font(FontFamily.named("Helvetica")))
  .margin(10)
  .beside(
    Picture.text("Gill Sans")
      .font(Font(FontFamily.named("Gill Sans")))
      .margin(10)
  )
  .beside(
    Picture.text("Garamond")
      .font(Font(FontFamily.named("Garamond")))
      .margin(10)
  )
  .beside(
    Picture.text("Adwaita Sans")
      .font(Font(FontFamily.named("Adwaita Sans")))
      .margin(10)
  )
  .noStroke
  .fillColor(Color.black)
```

Here's the output. What you will see depends on what fonts you have installed.

@:doodle("font-family-example", "SvgTextExamples.drawFontFamily") 


## Implementation

These methods are available on both the @:api(doodle.algebra.Text) algebra and @:api(doodle.image.Image).
