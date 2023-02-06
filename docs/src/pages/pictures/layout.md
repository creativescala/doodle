# Layout

## Concept

Positioning pictures relative to other pictures is important for man compositions, and the @:api(doodle.algebra.Layout) algebra provides a flexible system for handling this.


### Above, Beside, and On

The most basic layout methods are `above`, `beside`, and `on`. They do what their names suggest, putting a picture above, beside, or on another picture. Below is an example.

```scala mdoc:silent
import doodle.core._
import doodle.java2d._
import doodle.syntax.all._

val basicLayout =
  Picture
    .circle(100)
    .strokeColor(Color.blue)
    .beside(Picture.square(100).strokeColor(Color.darkBlue))
    .above(Picture.triangle(100, 100).strokeColor(Color.crimson))
    .strokeWidth(5.0)
```

Here's the output this creates.

@:image(basic-layout.png)

As a convenience, there are also methods `below` and `under`, which are the opposite of `above` and `on` respectively.


### Bounding Box and Origin

To understand how more advanced layout works we have to understand the concepts underlying layout: the bounding box and origin.


## Implementation
