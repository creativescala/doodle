# Text Examples

The Canvas backend fully supports [Text](../pictures/text.md). For example, here's a basic usage of text.

```scala 
import doodle.canvas.*

Picture.text("Hello from Doodle!")
  .fillColor(Color.black)
```

This produces the output below. Notice we specify a fill to get solid text.

@:doodle("text-hello", "CanvasTextExamples.drawHello")

We can fully style text, changing font, color, and so on. 

```scala
Picture
  .text("Change the font")
  .strokeColor(Color.blueViolet)
  .fillColor(Color.royalBlue)
  .font(Font.defaultSerif.bold.size(FontSize.points(24)))
```

This renders as shown below.

@:doodle("text-font", "CanvasTextExamples.drawFont")
