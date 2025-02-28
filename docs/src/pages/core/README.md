# Core

The `doodle.core` package provides utilities, such as @:api(doodle.core.Color) and @:api(doodle.core.Point), that are generally useful.
In this section we just cover the most important uses. You should see the @:api(doodle.core.index) for details.

## Imports

```scala mdoc:silent
// You definitely want doodle.core
import doodle.core.*
// You probably also want extension methods
import doodle.syntax.all.*
```


## Angle

The @:api(doodle.core.Angle) type represents an angle, as the name suggests.

Most of the time you'll create `Angles` using the extension methods shown below. Degrees and radians should be familiar, but turns may not be. One turn corresponds to a full circle (i.e. 360 degrees), so using turns in a convenient way to represent simple fractions or multiples of circles.

```scala mdoc:silent
45.degrees
1.radians
0.5.turns // One turn is a full circle, so this is half a circle
```

There are various methods to perform arithmetic on angles. Here are some examples. See the @:api(doodle.core.Angle) API for a complete list.

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

Working with @:api(doodle.core.Color) is something that most images will do. There are two representations of color used in Doodle:

* lightness, chroma, and hue ([OkLCh](https://en.wikipedia.org/wiki/Oklab_color_space)); and
* red, green, and blue (RGB).

You can use either representation interchangably. The OkLCh representation is easier to work with, while the RGB representation is how colors are actually generated by computer screens. All colors also have an alpha value, which determines transparency. Various constructors allow creating colors

```scala mdoc:silent
Color.oklch(0.5, 0.4, 0.degrees) // a vibrant red
Color.oklch(0.5, 0.4, 0.degrees, 0.5) // as above, but we also specify the alpha
Color.rgb(0, 0, 255) // pure blue
Color.rgb(0.uByte, 0.uByte, 255.uByte) // Using the UnsignedByte type
Color.rgb(0, 0, 255, 0.5) // Setting alpha
Color.rgb(0.uByte, 0.uByte, 255.uByte, 0.5.normalized) // Setting alpha
```

There are also constructors using the HSL (hue, saturation, lightness) color format, but it is preferred to use OkLCh.

```scala mdoc:silent
Color.hsl(0.degrees, 0.4, 0.4) // A red color using HSL
```

On the @:api(doodle.core.Color$) object all the standard CSS colors are defined. Here are a few examples.

```scala mdoc:silent
Color.steelBlue // Not to be confused with blue steel
Color.beige 
Color.limeGreen
```

You can also parse colors from CSS hex-color strings. For example:

```scala mdoc:silent
val red = Color.fromHex("#f00")
val green = Color.fromHex("#00ff00")
val transparentBlue = Color.fromHex("#0f09")
```

There are additional color palettes in @:api(doodle.core.TailwindColors) and @:api(doodle.core.CrayolaColors).


### Working with Colors

There are many methods to transform colors, such as `spin`, `desaturate`, and so on. See the @:api(doodle.core.Color) API for full details.
Transforming colors is where working with OkLCh colors really comes into its own.
OkLCh is designed for perceptual uniformity, meaning that colors with the same, say, lightness really do look like they have the same lightness.
This is not the case for colors specified using RGB or HSL.
For example, we can construct a gradient by changing only the hue of colors specified with both HSL and OkLCh.
This result is shown below, with the HSL gradient above the OkLCh gradient.
The OkLCh gradient is clearly smoother than than created using HSL.

@:image(gradients.png) {
  alt = A gradient created by changing only the hue using HSL and OkLCh colors
  title = A gradient created by changing only the hue using HSL and OkLCh colors
}


## Point

A @:api(doodle.core.Point) represents a location in the 2-D plane. We can construct points from cartesian (xy-coordinates) or polar (radius and angle) coordinates as shown below.

```scala mdoc
Point(1.0, 1.0) // cartesian coordinates
Point(1.0, 90.degrees) // polar coordinates
```

No matter how we construct a `Point` we can still access x- and y-coordinates or radius and angle.

```scala mdoc
val pt1 = Point(1.0, 0.0)
pt1.x
pt1.y
pt1.r
pt1.angle
```


## Transform

A @:api(doodle.core.Transform), in Doodle, represents an [affine transform](https://en.wikipedia.org/wiki/Affine_transformation) in two-dimensions. The easiest way to create a `Transform` is via the methods on the @:api(doodle.core.Transform$). Here are some examples.

```scala mdoc:silent
Transform.scale(5.0, -2.0)
Transform.rotate(90.degrees)
Transform.translate(10, 10)
```

A `Transform` can be applied to a `Point` to transform that point.

```scala mdoc
Transform.scale(5.0, -2.0)(Point(1,1))
Transform.rotate(90.degrees)(Point(1,1))
Transform.translate(10, 10)(Point(1,1))
```

`Transforms` can be composed together using the `andThen` method.

```scala mdoc
Transform.scale(5.0, -2.0).andThen(Transform.translate(10, 10))(Point(1,1))
Transform.scale(5.0, -2.0).translate(10, 10)(Point(1,1)) // Shorter version

```


## Vec

A @:api(doodle.core.Vec) represents a two-dimensional vector. You can construct `Vecs` from cartesian (xy-coordinates) or polar (length and angle) coordinates, just like `Point`.

```scala mdoc
Vec(0, 1)
Vec(1, 90.degrees)
```
