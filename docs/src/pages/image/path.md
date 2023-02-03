# Paths

Paths are a way to construct complex shapes. A path specifies how to control a virtual pen to draw a line. There are three different commands that a path can contain:

- straight lines;
- [bezier curves](https://en.wikipedia.org/wiki/B%C3%A9zier_curve); and
- straight line movement that doesn't draw to the screen.

Here's an example of creating a path. Notice that it has examples of all of the different components above.

```scala mdoc:silent
import doodle.core._
import doodle.syntax.all._

val path =
  ClosedPath.empty
    .lineTo(100, 100)
    .curveTo(90, 75, 90, 25, 10, 10)
    .moveTo(100, 100)
    .curveTo(75, 90, 25, 90, 10, 10)
```

Drawing this path creates the output below.

@:image(open-path.png)

You probably noticed this is a `ClosedPath`, which suggests there is also an `OpenPath`. This is correct. The difference is how the path ends. If a `ClosedPath` doesn't end at the point it started (which is the origin), it will have a straight line inserted joining the end to the start. An `OpenPath` will not.

