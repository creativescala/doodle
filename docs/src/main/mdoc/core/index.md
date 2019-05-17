# Core

The @scaladoc[core](doodle.core.index) package provides utilities, such as @scaladoc[Color](doodle.core.Color) and @scaladoc[Point](doodle.core.Point), that are useful in the rest of the libraries.

In this section we just cover the most important uses. You should see the @scaladoc[ScalaDoc](doodel.core.index) for details.

## Imports

```scala mdoc:silent
// You definitely want doodle.core
import doodle.core._
// You probably also want extension methods
import doodle.syntax._
```


## Angle

The @scaladoc[Angle](doodle.core.Angle) type represents an angle, as the name suggests.

Most of the time you'll create `Angles` using the extension methods shown below. Degrees and radians should be familiar, but turns may not be. One turn corresponds to a full circle (i.e. 360 degrees), so using turns in a convenient way to represent simple fractions or multiples of circles.

```scala mdoc:silent
45.degrees
1.radians
0.5.turns // One turn is a full circle, so this is half a circle
```

There are various methods to perform arithmetic on angles. Here are some examples. See the @scaladoc[ScalaDoc](doodle.core.Angle) for a complete list.

```scala mdoc
(45.degrees + 45.degrees) < 180.degrees
(45.degrees * 2) < 90.degrees
180.degrees - 0.5.turns
```

Other useful methods are calculating the sine and cosine of an angle, and normalizing an angle to between zero and 360 degrees.

```scala mdoc
0.5.turns.sin
0.5.turns.cos
2.turns.normalize == 1.turns
```


## Color

Working with @scaladoc[Color](doodle.core.Color) is something that most images will do. There are two representations of color used in Doodle:

* hue, saturation, and lightness (HSL); and
* red, green, and blue (RGB).

The HSL representation is easier to work with, while the RGB representation is how colors are actually generated by computer screens. All colors also have an alpha value, which determines transparency. Various constructors allow creating colors

```scala mdoc:silent
Color.hsl(0.degrees, 1.0, 0.5) // a vibrant red
Color.hsla(0.degrees, 1.0, 0.5, 0.5) // set the alpha to 0.5 (half transparent)
Color.rgb(0, 0, 255) // pure blue
Color.rgb(0.uByte, 0.uByte, 255.uByte) // Using the UnsignedByte type
Color.rgba(0, 0, 255, 0.5) // Setting alpha
Color.rgba(0.uByte, 0.uByte, 255.uByte, 0.5.normalized) // Setting alpha
```

On the @scaladoc[Color companion object](doodle.core.Color$) all the standard CSS colors are defined. Here are a few examples.

```scala mdoc:silent
Color.steelBlue // Not to be confused with blue steel
Color.beige 
Color.limeGreen
```

There are many methods to modify colors, such as `spin`, `desaturate`, and so on. See the @scaladoc[ScalaDoc](doodle.core.Color) for full details.


## Point

## Transform

## Vec