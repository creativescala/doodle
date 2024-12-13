# Doodle Java2D

Doodle Java2D draws Doodle pictures using [Java 2D](https://www.oracle.com/java/technologies/java-2d-api.html), the graphic library that ships with the JVM.


## Usage

Firstly, import the Doodle core and syntax, the Java2D definitions, and the default Cats Effect runtime.

```scala mdoc:silent
import cats.effect.unsafe.implicits.global
import doodle.core.*
import doodle.java2d.*
import doodle.syntax.all.*
```

Now you can draw `Pictures` like so:

```scala mdoc:compile-only
Picture.circle(100).draw()
```

The drawing will appear in a window on the screen.

You can define a @:api(doodle.java2d.effect.Frame) to have more control over the appearance of the window in which a `Picture` is drawn.
The `Frame`  allows you to specify, for example, the title for the window, the background color, or the size of the window.
Here's an example:

```scala mdoc:silent
val frame =
  Frame.default
    .withSize(600, 600)
    .withCenterAtOrigin
    .withBackground(Color.midnightBlue)
    .withTitle("Oodles of Doodles!")
```

This sets:

- the size of the window to 600 pixels wide and high;
- the center of the window to the origin (0, 0), instead of the center of the bounding box of the `Picture` that is rendered;
- the background color to midnight blue; and
- the window's title to "Oodles of Doodles!"

We can drawn to a frame using the `drawWithFrame` syntax.

```scala mdoc:compile-only
Picture.circle(100).drawWithFrame(frame)
```


## Canvas

The Java2D @:api(doodle.java2d.effect.Canvas) offers several features that are useful for interactive programs.
It provides the following streams of events:

* `mouseClick`, which is a `Stream[IO, Point]` producing the position of mouse clicks;
* `mouseMove`, which is a `Stream[IO, Point]` producing the position of the mouse cursor and updates when the mouse moves;
* `redraw`, which is a `Stream[IO, Int]` producing a value approximately every 16.67ms (i.e. at a rate of 60fps). The value is the duration, in milliseconds, since the previous event.

Also available are

* `closed`, an `IO[Unit]` that produces a value when the window has been closed; and
* `close()`, a method to programmatically close the window instead of waiting for the user to do so.

To create a `Canvas` you call the `canvas()` method on a `Frame`.
The returns a `Resource` that will produce the canvas when `use`d.
Here's a small example:

```scala mdoc:compile-only
frame
  .canvas()
  .use { canvas =>
    // Do stuff here
    // This example just closes the canvas
    canvas.close()
  }
```

`
