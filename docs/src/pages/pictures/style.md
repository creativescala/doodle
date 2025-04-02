# Style

## Concept

The @:api(doodle.algebra.Style) algebra provides methods to style pictures. There are two main ways in which a picture can be styled:

- the stroke, which specifies the outline of a picture; and
- the fill, which specifies what goes inside a picture.

The default is a black stroke and an empty fill.

The example below shows an unstyled circle next to a circle with a stroke color, stroke width, and a fill color.

```scala mdoc:silent
import doodle.core.*
import doodle.java2d.*
import doodle.syntax.all.*

val basicStyle =
  Picture
    .circle(100)
    .beside(
      Picture
        .circle(100)
        .strokeColor(Color.purple)
        .strokeWidth(5.0)
        .fillColor(Color.lavender)
    )
```

@:image(basic-style.png) {
  alt = "A circle with no style, and a circle with a purple stroke and lavender fill"
  title = "A circle with no style, and a circle with a purple stroke and lavender fill"
}

The available settings for the stroke are:

- the color, `strokeColor`, or gradient, `strokeGradient`;
- the width, `strokeWidth`;
- the @:api(doodle.core.Cap), `strokeCap`, which specifies how lines end;
- the @:api(doodle.core.Join), `strokeJoin`, which specifies how lines are joined together; and
- the dash, `strokeDash`, which specifies a pattern to be used for the stroke.

If you don't want any stroke, you can use `noStroke`.

The example below shows some of the variation available with stroke styling.


```scala mdoc:silent
val strokeStyle =
  Picture
    .star(5, 50, 25)
    .strokeWidth(5.0)
    .strokeColor(Color.limeGreen)
    .strokeJoin(Join.bevel)
    .strokeCap(Cap.round)
    .strokeDash(Array(9.0, 7.0))
    .beside(
      Picture
        .star(5, 50, 25)
        .strokeWidth(5.0)
        .strokeColor(Color.greenYellow)
        .strokeJoin(Join.miter)
        .strokeCap(Cap.square)
        .strokeDash(Array(12.0, 9.0))
    )
    .beside(
      Picture
        .star(5, 50, 25)
        .strokeWidth(5.0)
        .strokeGradient(
          Gradient.dichromaticVertical(Color.crimson, Color.midnightBlue, 50)
        )
        .strokeJoin(Join.round)
        .strokeCap(Cap.butt)
        .strokeDash(Array(15.0, 5.0))
    )
```

@:image(stroke-style.png) {
  alt = Two stars with different stroke styles
  title = Two stars with different stroke styles
}

The fill can either be one of:

- a solid color, specified with `fillColor`;
- a @:api(doodle.core.Gradient), specified with `fillGradient`;
- no fill, specified with `noFill`.

The example below shows examples of using gradient fills.

```scala mdoc:silent
val fillStyle =
  Picture
    .square(100)
    .fillGradient(
      Gradient.linear(
        Point(-50, 0),
        Point(50, 0),
        List((Color.red, 0.0), (Color.yellow, 1.0)),
        Gradient.CycleMethod.repeat
      )
    )
    .strokeWidth(5.0)
    .margin(0.0, 5.0, 0.0, 0.0)
    .beside(
      Picture
        .circle(100)
        .fillGradient(
          Gradient.dichromaticRadial(Color.limeGreen, Color.darkBlue, 50)
        )
        .strokeWidth(5.0)
    )
```

@:image(fill-style.png) {
  alt = "A square with a linear gradient fill, and a circle with a radial gradient fill"
  title = "A square with a linear gradient fill, and a circle with a radial gradient fill"
}


## Implementation

The @:api(doodle.algebra.Style) algebra supports all the features described above as does @:api(doodle.image.Image).
