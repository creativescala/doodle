# Paths

Paths are a way to construct complex shapes,
implemented by the @:api(doodle.algebra.Path) algebra and the @:api(doodle.core.OpenPath) and @:api(doodle.core.ClosedPath) types. 
A path specifies how to control a virtual pen to draw a shape. 
There are three different commands that a path can contain:

- straight lines;
- [bezier curves](https://en.wikipedia.org/wiki/B%C3%A9zier_curve); and
- straight line movement that doesn't draw to the screen.

Here's an example of a path. Notice that it uses all of the different commands described above.

```scala mdoc:silent
import doodle.core.*
import doodle.java2d.*
import doodle.syntax.all.*

val feather =
  ClosedPath.empty
    .lineTo(100, 100)
    .curveTo(90, 75, 90, 25, 10, 10)
    .moveTo(100, 100)
    .curveTo(75, 90, 25, 90, 10, 10)
    .toPicture
```

Drawing this path creates the output below.

@:image(basic-path.png) {
  alt = A path with straight lines and bezier curves 
  title = A path with straight lines and bezier curves
}


## Open and Closed Path

The example above is a `ClosedPath`, which suggests there is also an `OpenPath`. This is correct. The difference is how the path ends. If a `ClosedPath` doesn't end at the point it started (which is the origin), it will have a straight line inserted joining the end to the start. An `OpenPath` will not.

Here's an example showing the difference. We create a curve, drawn as both an open and a closed path. 

```scala mdoc:silent
val open =
  OpenPath.empty.curveTo(90, 0, 100, 10, 50, 50).toPicture.strokeColor(Color.red)

val closed =
  ClosedPath.empty.curveTo(90, 0, 100, 10, 50, 50).toPicture.strokeColor(Color.blue)

val curves = open.beside(closed)
```

Notice how the open path is just the curve, while the closed path has an extra line added joining the start and end points.

@:image(open-closed-paths.png) {
  alt = "A curve drawn as an open and as a closed path"
  title = "A curve drawn as an open and as a closed path"
}


## Creating Paths

The idiomatic way to create a path is by calling the `empty` method on either `ClosedPath` or `OpenPath`, and then calling the `lineTo`, `curveTo`, and `moveTo` methods on the resulting object. This is the method used in all the examples we've seen so far. An alternative approach is to creates instances of @:api(doodle.core.PathElement) and create a path from a `List[PathElement]`.

The `close` method converts an `OpenPath` to a `ClosedPath`, and the `open` method does the reverse.


## Creating Pictures from Paths

Use the `toPicture` method to convert a path to a `Picture`, as demonstrated in the examples above.
There are two other ways to do this, which have been superceded by the `toPicture` method, but you might still see in older code: using the `path` method in place of `toPicture`, or calling the `path` method on the `Picture` object.

To create an `Image` from a path, use the `path` method on `Image`. Here's an example.

```scala mdoc:silent
import doodle.image.*

val openPath = OpenPath.empty.lineTo(100, 100)

val closedPath = ClosedPath.empty.lineTo(100, 100)

val path1 = Image.path(openPath)
val path2 = Image.path(closedPath)
```


## Utilities

There are several utilities to create common shapes. For example, @:api(doodle.core.OpenPath) has methods to create arcs and regular polygons, and @:api(doodle.core.ClosedPath) has methods to create circles and pie slices. See the companion objects for both for the complete list.
Many of these also exist as methods on @:api(doodle.core.PathElement).

One particularly useful method is `interpolatingSpline`, which creates a curve that intersects a given sequence of points. In the example below we create a list of points from a sine curve, and interpolate a spline between the points.

```scala mdoc:silent
val points =
  for (x <- 0.to(360)) yield Point(x, x.degrees.sin * 100)

val curve = Picture.interpolatingSpline(points.toList)
```

The output, shown below, shows that a smooth curve is created.

@:image(curve.png) {
  alt = A curve that intersects a sequence of points
  title = A curve that intersects a sequence of points
}


## Implementation

`Image` fully supports path. The underlying algebra is @:api(doodle.algebra.Path). @:api(doodle.core.ClosedPath), @:api(doodle.core.OpenPath), and @:api(doodle.core.PathElement) are all part of `doodle.core`, and not tied to either algebras or `Image`.

