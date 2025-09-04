# Picture and Image

There are two APIs you can use to create pictures: @:api(doodle.algebra.Picture) and @:api(doodle.image.Image). This section describes how to use and how to choose between them.


## Choosing Between Picture and Image

We recommend using `Image` if you are new to Scala. It's written in a simple style, so you will have an easier time understanding how to use it and fixing any problems that may arise.
If you're a more experienced Scala user we suggest using `Picture`. It allows access to platform specific features not available on `Image`.

If you have created a picture using `Image` and find that you need the power of `Picture`, all you need to do is:

1. change the imports as described below; and
2. replace all occurrences of `Image` with `Picture`.

This process will also work in reverse for many pictures. The examples in the documentation mostly use `Picture` but you can convert the majority of them to `Image` as described here.


## Using Image

To use `Image` we need the following imports:

```scala mdoc:silent
import doodle.core.*
import doodle.image.*
import doodle.image.syntax.all.*
```

We'll also need to import a backend. Most people will be working on the JVM, and will use the Java2D backend.

```scala mdoc:silent
import doodle.java2d.*
import cats.effect.unsafe.implicits.global
```

`Image` is based on *composition* and the *interpreter pattern*. 

Composition basically means that we build big images out of small images. For example, if we have an image describing a red square and an image describing a blue square

```scala mdoc:silent
val redSquare = Image.square(100).fillColor(Color.red)
val blueSquare = Image.square(100).fillColor(Color.blue)
```

we can create an image describing a red square next to a blue square by combining, or composing, them together.

```scala mdoc:silent
val composition = redSquare.beside(blueSquare)
```

Finally, we can draw the image by calling the `draw` method.

```scala mdoc:compile-only
composition.draw()
```

When drawn it has the output shown below. 

@:image(composition.png) {
  alt = A red square beside a blue square
  title = A red square beside a blue square
}

The interpreter pattern means that we separate describing the `Image` from rendering it. Writing `Image.square(100)` doesn't draw anything. To draw an image we need to call the `draw()` method. This separation is important for composition; if we were to immediately draw we would lose composition. 


## Using Picture

To use `Picture` we need the following imports:

```scala mdoc:reset:silent
import doodle.core.*
import doodle.syntax.all.*
```

We'll also need to import a backend. Most people will be working on the JVM, and will use the Java2D backend.

```scala mdoc:silent
import doodle.java2d.*
import cats.effect.unsafe.implicits.global
```

`Picture` works the same way as `Image`, using composition and the interpreter pattern. In fact the above example can be rewritten using picture by simply changing `Image` to `Picture`.

```scala mdoc:silent
val redSquare = Picture.square(100).fillColor(Color.red)
val blueSquare = Picture.square(100).fillColor(Color.blue)
val composition = redSquare.beside(blueSquare)
```

To draw the `Picture` we call the `draw()` exactly as we would with `Image`.

```scala mdoc:compile-only
composition.draw()
```
