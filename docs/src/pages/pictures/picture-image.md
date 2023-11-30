# Picture and Image

There are two types you can use to create pictures: @:api(doodle.algebra.Picture) and @:api(doodle.image.Image).

This section describes how to use and how to choose between them.


## Choosing Between Picture and Image

If you're new to Scala, `Image` is probably the best choice for you. It's written in a simple style, so you will have an easier time understanding how to use it and any error messages that may arise.

If you're a more experienced Scala user you could choose `Picture`. It allows access to platform specific features not available on `Image`.


## Using Image

To use `Image` we need the following imports:

```scala mdoc:silent
import doodle.core.*
import doodle.image.*
```
```scala
import doodle.image.syntax.all.*
```

We'll also need to import a backend. Most people will be working on the JVM, and will use the Java2D backend.

```scala
import doodle.java2d.*
import cats.effect.unsafe.implicits.global
```

`Image` is based on *composition* and the *interpreter pattern*. 

Composition basically means that we build big images out of small images. For example, if we have an image describing a red square and an image describing a blue square

```scala mdoc:silent
val redSquare = Image.square(100).fillColor(Color.red)
val blueSquare = Image.square(100).fillColor(Color.blue)
```

we can create an image describing a red square next to a blue square by combining or composing them together.

```scala mdoc:silent
val composition = redSquare.beside(blueSquare)
```

When draw this has the output shown below. 

@:image(red-blue.png) {
  alt = A red square beside a blue square
  title = A red square beside a blue square
}

The interpreter pattern means that we separate describing the Image from rendering it. Writing `Image.square(100)` doesn't draw anything. To draw an image we need to call the `draw()` method. This separation is important for composition; if we were to immediately draw we would lose composition. 


## Using Picture

To use `Picture` we need the following imports:

```scala mdoc:reset:silent
import doodle.core.*
import doodle.syntax.all.*
```

We'll also need to import a backend. Most people will be working on the JVM, and will use the Java2D backend.

```scala mdoc:silent
import doodle.java2d.*
```
```scala
import cats.effect.unsafe.implicits.global
```

`Picture` works the same way as `Image`, using composition and the interpreter pattern. In fact the above example can be rewritten using picture by simply changing `Image` to `Picture`.

```scala mdoc:silent
val redSquare = Picture.square(100).fillColor(Color.red)
val blueSquare = Picture.square(100).fillColor(Color.blue)
val composition = redSquare.beside(blueSquare)
```

To draw the `Picture` we call the `draw()` exactly as we would with `Image`.
