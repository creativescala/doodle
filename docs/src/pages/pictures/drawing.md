# Drawing to the Screen

Drawing a picture to the screen is usually the end goal of using Doodle.
The usual way to draw a `Picture` is by calling the `draw` method. Using the Java2D backend will produce output in a window, while the SVG backend can produce output inside a web page.

The examples below use the Java2D backend, but the general principles work with other backends.


## Imports

You need the normal imports to do anything with Doodle. Here are the imports for `Picture` and the JVM backend.

```scala mdoc:silent
import doodle.core.*
import doodle.syntax.all.*
import doodle.java2d.*
import cats.effect.unsafe.implicits.global
```

For some of the examples below we also need

```scala mdoc:silent
import cats.effect.{IO, Resource}
```


## Drawing

The normal way to draw output is by calling the `draw` method.

```scala mdoc:silent
val picture = Picture.circle(100).strokeColor(Color.crimson)
```

```scala
picture.draw()
```

When using the Java2D, the output will appear in separate window.

As explained [in the concepts chapter](../concepts/frame-canvas.md), Doodle has the concept of a frame and a canvas. A canvas is an area where a picture can be drawn, and a frame describes how to create a canvas. If you don't explicitly tell Doodle what frame you want, it uses a default. The default has a white background, is a little bit larger than the picture being drawn, and centers the picture in the middle of it.

The Java2D @:api(doodle.java2d.effect.Frame) allows you specify the size of the window, a background color, and more. Here's an example where we change the size and background color.

```scala mdoc:silent
val frame = 
  Frame.default.withSize(300, 300).withBackground(Color.midnightBlue)
```

Once we have a frame, we can pass it to `drawWithFrame`.

```scala
picture.drawWithFrame(frame)
```

Sometimes you'll want to draw several pictures on the same canvas. By repeatedly drawing to the same canvas we can, for example, create animations. To do this we need to work with the underling `IO` and `Resource` types that the conveniences above hide.


## Drawing with IO and Resource

It can be useful to convert a `Picture[A]` to an `IO[A]`. This could be because the picture is part of a larger program using `IO`, you are working with an `Resource[IO, Canvas]`, or you want to access the `A` value that is discarded by the `draw` variants discussed above. The `drawToIO` method does this conversion.

```scala mdoc:silent
picture.drawToIO()
```

There are also variants `drawWithFrameToIO` and `drawWithCanvasToIO`.

```scala mdoc:silent
picture.drawWithFrameToIO(frame)
```

Using `drawWithCanvasToIO` requires we get hold of a `Canvas`. We call the `canvas()` method on a `Frame` to do so. This returns a `Resource[IO, Canvas]`.

```scala mdoc:silent
val canvas: Resource[IO, Canvas] = frame.canvas()
```

When we have a `Resource[IO, Canvas]` we can `use` it to access the `Canvas` it manages. This is the idiomatic way to work with a `Resource[IO, Canvas]`.

```scala mdoc:silent
canvas.use(c => picture.drawWithCanvasToIO(c))
```

Once you have an `IO`, you can run it in the usual way as part of an `IOApp`, with `unsafeRunSync`, or one of the other methods.
