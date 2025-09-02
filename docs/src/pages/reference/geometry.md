# Geometry

```scala mdoc:invisible
import doodle.core.*
import doodle.syntax.all.*
```

Doodle provides many utilities for working with two-dimensional geometry.


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
