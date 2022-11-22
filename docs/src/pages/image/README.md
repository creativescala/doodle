# Image

The @:api(doodle.image.index) library is the easiest way to create images using Doodle. The tradeoff the Image library makes is that it only support a (large but limited) subset of operations that are supported across all the backends.

## Imports

To use Image you'll need the following imports:

```scala mdoc
import doodle.image._
import doodle.image.syntax.all._
import doodle.core._
```

You'll also need to import a backend. Most people will be working on the JVM, and will use the Java2D backend:

```scala mdoc
import doodle.java2d._
import cats.effect.unsafe.implicits.global
```


## Basic Concepts

Image is based on *composition* and the *interpreter pattern*. 

Composition basically means that we build big Images out of small Images. For example, if we have an Image describing a red square and an Image describing a blue square

```scala mdoc:silent
val redSquare = Image.square(100).fillColor(Color.red)
val blueSquare = Image.square(100).fillColor(Color.blue)
```

we can create an Image describing a red square next to a blue square by combining them together.

```scala mdoc:silent
val combination = redSquare.beside(blueSquare)
```

The interpreter pattern means that we separate describing the Image from rendering it. Writing `Image.square(100)` doesn't draw anything. To draw an image we need to call the `draw()` method. This separation is important for composition; if we were to immediately draw we would lose composition. 


